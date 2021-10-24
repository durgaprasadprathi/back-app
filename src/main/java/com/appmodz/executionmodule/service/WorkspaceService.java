package com.appmodz.executionmodule.service;
import com.appmodz.executionmodule.dao.*;
import com.appmodz.executionmodule.dto.*;
import com.appmodz.executionmodule.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    @Autowired
    WorkspaceDAO workspaceDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    OrganizationDAO organizationDAO;

    @Autowired UtilService utilService;

    public Workspace getWorkspaceById(long workspaceId) throws Exception{
        Workspace workspace = workspaceDAO.get(workspaceId);
        if(workspace==null)
            throw new Exception("No workspace with this workspace id exists");
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(workspace.getOrganization().getOrganizationId())
                        .userId(workspace.getOwner().getUserId())
                        .build()
                , "GET_WORKSPACE"))
            throw new Exception("GET WORKSPACE ACTION NOT PERMITTED FOR THIS USER");
        return workspace;
    }

    public Workspace createWorkspace(WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        Workspace workspace = new Workspace();
        User user = userDAO.get(workspaceRequestDTO.getOwnerId());
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(workspaceRequestDTO.getOrganizationId())
                        .userId(workspaceRequestDTO.getOwnerId())
                        .build()
                , "CREATE_WORKSPACE"))
            throw new Exception("CREATE WORKSPACE ACTION NOT PERMITTED FOR THIS USER");
        workspace.setOwner(user);
        workspace.setWorkspaceName(workspaceRequestDTO.getName());
        Organization organization = organizationDAO.get(workspaceRequestDTO.getOrganizationId());
        workspace.setOrganization(organization);
        workspaceDAO.save(workspace);
        return workspace;
    }

    public Workspace editWorkspace(WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        Workspace workspace = workspaceDAO.get(workspaceRequestDTO.getId());
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(workspace.getOrganization().getOrganizationId())
                        .userId(workspace.getOwner().getUserId())
                        .build()
                , "UPDATE_WORKSPACE"))
            throw new Exception("UPDATE WORKSPACE ACTION NOT PERMITTED FOR THIS USER");
        if(workspaceRequestDTO.getOwnerId()!=null&&workspaceRequestDTO.getOrganizationId()!=null) {
            User user = userDAO.get(workspaceRequestDTO.getOwnerId());
            Organization organization = organizationDAO.get(workspaceRequestDTO.getOrganizationId());
            if (user.getUserOrganization().getOrganizationId()!=organization.getOrganizationId()) {
                throw new Exception("Workspace owner belongs to different organization");
            }
        }
        else if(workspaceRequestDTO.getOwnerId()!=null) {
            User user = userDAO.get(workspaceRequestDTO.getOwnerId());
            if (user.getUserOrganization().getOrganizationId()!=workspace.getOrganization().getOrganizationId()) {
                throw new Exception("Workspace owner belongs to different organization");
            }
            workspace.setOwner(user);
        }
        else if(workspaceRequestDTO.getOrganizationId()!=null) {
            Organization organization = organizationDAO.get(workspaceRequestDTO.getOrganizationId());
            if (workspace.getOwner().getUserOrganization().getOrganizationId()!=workspaceRequestDTO.getOrganizationId()) {
                throw new Exception("Workspace owner belongs to different organization");
            }
            workspace.setOrganization(organization);
        }
        if(workspaceRequestDTO.getName()!=null) {
            workspace.setWorkspaceName(workspaceRequestDTO.getName());
        }
        workspaceDAO.save(workspace);
        return workspace;
    }


    public void deleteWorkspace(Long id) throws Exception{
        Workspace workspace = workspaceDAO.get(id);
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(workspace.getOrganization().getOrganizationId())
                        .userId(workspace.getOwner().getUserId())
                        .build()
                , "DELETE_WORKSPACE"))
            throw new Exception("DELETE WORKSPACE ACTION NOT PERMITTED FOR THIS USER");
        workspaceDAO.delete(workspace);
    }

    public String deleteMultipleWorkspaces(WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        List<Long> ids = workspaceRequestDTO.getIds();
        StringBuilder exceptions = new StringBuilder();
        StringBuilder successes = new StringBuilder();
        for (long id: ids) {
            Workspace workspace = workspaceDAO.get(id);
            if(!utilService.checkPermission(
                    PermissionDTO.builder()
                            .organizationId(workspace.getOrganization().getOrganizationId())
                            .userId(workspace.getOwner().getUserId())
                            .build()
                    , "DELETE_WORKSPACE"))
                exceptions.append("DELETE WORKSPACE ACTION NOT PERMITTED FOR THIS USER FOR ID").append(id);
            try {
                workspaceDAO.delete(workspace);
                successes.append("Successfully Deleted ").append(id).append("\n");
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                exceptions.append("Unable To Delete id ").append(id).append(" Due To Possible Foreign Key associations\n");
            }
        }

        if(exceptions.toString().length()>0)
            throw new Exception(exceptions.toString());

        return successes.toString();
    }

    public List listWorkspaces() {
        List<Workspace> workspaces = workspaceDAO.getAll();
        workspaces =  workspaces.stream().filter(w->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(w.getOrganization().getOrganizationId())
                        .userId(w.getOwner().getUserId())
                        .build(),
                "GET_WORKSPACE")).collect(Collectors.toList());
        return workspaces;
    }

    public SearchResultDTO searchWorkspaces(SearchRequestDTO searchRequestDTO) {
        SearchResultDTO searchResultDTO = workspaceDAO.search(searchRequestDTO);
        searchResultDTO.setData(
                (List)searchResultDTO.getData().stream().filter(w->utilService.checkPermission(
                        PermissionDTO.builder()
                                .organizationId(((Workspace)w).getOrganization().getOrganizationId())
                                .userId(((Workspace)w).getOwner().getUserId())
                                .build()
                        ,
                        "GET_WORKSPACE")).collect(Collectors.toList())
        );
        return searchResultDTO;
    }
}
