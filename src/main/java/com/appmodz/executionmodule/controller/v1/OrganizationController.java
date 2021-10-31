package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dto.OrganizationRequestDTO;
import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.dto.SearchResultDTO;
import com.appmodz.executionmodule.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("v1OrganizationController")
@RequestMapping("/v1/organizations")
public class OrganizationController {

    @Autowired
    OrganizationService organizationService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    @ResponseBody
    public Object getOrganizations(@RequestParam(required = false) String format, HttpServletResponse response)
            throws Exception{
        if(format!=null) {
            if(format.equals("xlsx")) {
                organizationService.exportOrganizations(response);
            }
            return new ResponseDTO("success",null,null);
        }return new ResponseDTO("success",null,
                organizationService.listOrganizations());
    }


    @RequestMapping(value="/{id}",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getOrganization(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    organizationService.getOrganizationById(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json",  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createAndSearchOrganizations(@RequestBody OrganizationRequestDTO organizationRequestDTO) throws Exception {
        if(organizationRequestDTO!=null&&organizationRequestDTO.getSearch()!=null){
            return new ResponseDTO("success",null,
                    organizationService.searchOrganizations(organizationRequestDTO));
        } else if (organizationRequestDTO.getName()!=null) {
            return new ResponseDTO("success",null,organizationService.createOrganization(organizationRequestDTO.getName()));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Object importOrganization(OrganizationRequestDTO organizationRequestDTO) throws Exception{
        if(organizationRequestDTO.getFile()!=null) {
            organizationService.importOrganizations(organizationRequestDTO.getFile());
            return new ResponseDTO("success",null,null);
        }
        return new ResponseDTO("failure","File not present",null);
    }

    @RequestMapping(value="/{id}",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object updateOrganizations(@PathVariable Long id,
                                      @RequestBody OrganizationRequestDTO organizationRequestDTO) throws Exception {
        if (organizationRequestDTO.getName()!=null&&id!=null) {
            return new ResponseDTO("success",null,
                    organizationService.updateOrganization(id, organizationRequestDTO.getName()));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/{id}",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganization(@PathVariable Long id) throws Exception{
        if (id!=null) {
            organizationService.deleteOrganization(id);
            return new ResponseDTO("success",null, null);
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganizationS(@RequestBody OrganizationRequestDTO organizationRequestDTO) throws Exception{
        if (organizationRequestDTO.getIds()!=null) {
            return new ResponseDTO("success",null, organizationService.deleteMultipleOrganizations(organizationRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }
}
