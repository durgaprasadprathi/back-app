package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.OrganizationDAO;
import com.appmodz.executionmodule.dto.*;
import com.appmodz.executionmodule.model.Organization;
import com.appmodz.executionmodule.model.Workspace;
import com.appmodz.executionmodule.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    @Autowired
    OrganizationDAO organizationDAO;

    @Autowired
    UtilService utilService;

    public Organization getOrganizationById(long organizationId) throws Exception{
        Organization organization = organizationDAO.get(organizationId);
        if(organization==null)
            throw new Exception("Organization Not Found");
        else if (!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(organization.getOrganizationId())
                        .build()
               , "GET_ORGANIZATION"))
            throw new Exception("GET ORGANIZATION ACTION NOT PERMITTED FOR THIS USER");
        return organizationDAO.get(organizationId);
    }

    public Organization createOrganization(String name) throws Exception{
        if (!utilService.checkPermission(PermissionDTO.builder().build(), "CREATE_ORGANIZATION"))
            throw new Exception("CREATE ORGANIZATION ACTION NOT PERMITTED FOR THIS USER");
        Organization organization = new Organization();
        organization.setOrganizationName(name);
        organizationDAO.save(organization);
        return organization;
    }

    public Organization updateOrganization(long organizationId, String name) throws Exception {
        Organization organization = organizationDAO.get(organizationId);
        if (!utilService.checkPermission(PermissionDTO.builder()
                .organizationId(organization.getOrganizationId())
                .build(), "UPDATE_ORGANIZATION"))
            throw new Exception("UPDATE ORGANIZATION ACTION NOT PERMITTED FOR THIS USER");
        organization.setOrganizationName(name);
        organizationDAO.save(organization);
        return organization;
    }

    public void deleteOrganization(long id) throws Exception{
        if (!utilService.checkPermission(PermissionDTO.builder()
                .organizationId(id)
                .build(), "DELETE_ORGANIZATION"))
            throw new Exception("DELETE ORGANIZATION ACTION NOT PERMITTED FOR THIS USER");
        Organization organization = organizationDAO.get(id);
        organizationDAO.delete(organization);
    }

    public String deleteMultipleOrganizations(OrganizationRequestDTO organizationRequestDTO) throws Exception{
        List<Long> ids = organizationRequestDTO.getIds();
        StringBuilder exceptions = new StringBuilder();
        StringBuilder successes = new StringBuilder();
        for (long id: ids) {
            Organization organization = organizationDAO.get(id);
            if (!utilService.checkPermission(PermissionDTO.builder()
                    .organizationId(id)
                    .build(), "DELETE_ORGANIZATION"))
                exceptions.append("DELETE ORGANIZATION ACTION NOT PERMITTED FOR THIS USER FOR ID").append(id);
            try {
                organizationDAO.delete(organization);
                successes.append("Successfully Deleted ").append(id).append("\n");
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                exceptions.append("Unable To Delete id ").append(id).append(" Due To Possible Foreign Key associations").append("\n");
            }
        }

        if(exceptions.toString().length()>0)
            throw new Exception(exceptions.toString());

        return successes.toString();
    }

    public List listOrganizations() {
        List<Organization> organizations = organizationDAO.getAll();
        organizations =  organizations.stream().filter(o->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(o.getOrganizationId())
                        .build(),
                "GET_ORGANIZATION")).collect(Collectors.toList());
        return organizations;
    }

    public SearchResultDTO searchOrganizations(SearchRequestDTO searchRequestDTO) {
        SearchResultDTO searchResultDTO = organizationDAO.search(searchRequestDTO);
        searchResultDTO.setData(
                (List)searchResultDTO.getData().stream().filter(o->utilService.checkPermission(
                        PermissionDTO.builder()
                                .organizationId(((Organization)o).getOrganizationId())
                                .build(),
                        "GET_ORGANIZATION")).collect(Collectors.toList())
        );
        return searchResultDTO;
    }
    public void importOrganizations(MultipartFile multipartFile) throws Exception {
        ExcelUtil excelUtil = new ExcelUtil(multipartFile.getInputStream());
        Object[][] data = excelUtil.readSheet("Organizations");
        for(int i=1;i< data.length;i++) {
            Organization organization = new Organization();
            for(int j=0;j<data[i].length;j++) {
                Object obj = data[i][j];
                switch (j) {
                    case 0:
                        if(obj!=null)
                            organization.setOrganizationId((Long) obj);
                        break;
                    case 1:
                        if(obj!=null) {
                            organization.setOrganizationName((String) obj);
                        }
                        break;
                    case 2:
                        if(obj!=null) {
                            organization.setOrganizationCreatedOn((Date) obj);
                        }
                        break;
                    case 3:
                        if(obj!=null) {
                            organization.setOrganizationUpdatedOn((Date) obj);
                        }
                        break;
                }
            }
            organizationDAO.save(organization);
        }
    }

    public void exportOrganizations(HttpServletResponse response) throws IOException {
        List<Organization> organizations = organizationDAO.getAll();
        organizations =  organizations.stream().filter(o->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(o.getOrganizationId())
                        .build(),
                "GET_ORGANIZATION")).collect(Collectors.toList());
        Object[][] list = new Object[organizations.size()+1][];
        list[0] = new Object[]{"OrganizationId",
                "OrganizationName",
                "OrganizationCreatedOn"
                ,"OrganizationUpdatedOn"};
        for(int i=0;i<organizations.size();i++) {
            Organization organization = organizations.get(i);
            list[i+1] = new Object[]{
                    organization.getOrganizationId(),
                    organization.getOrganizationName(),
                    organization.getOrganizationCreatedOn(),
                    organization.getOrganizationUpdatedOn()
            };
        }
        ExcelUtil excelUtil = new ExcelUtil();
        XSSFWorkbook xssfWorkbook = excelUtil.createSheet("Organizations",list);
        response.setHeader("Content-Disposition", "attachment; filename=organizations.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        xssfWorkbook.write(outputStream);
        xssfWorkbook.close();
        outputStream.close();
    }


}
