package com.appmodz.executionmodule.controller.v1;
import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.dto.WorkspaceRequestDTO;
import com.appmodz.executionmodule.service.StackService;
import com.appmodz.executionmodule.service.UserService;
import com.appmodz.executionmodule.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("v1WorkspaceController")
@RequestMapping("/v1/workspaces")
public class WorkspaceController {
    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    StackService stackService;

    @Autowired
    UserService userService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    @ResponseBody
    public Object getWorkspaces(@RequestParam(required = false) String format, HttpServletResponse response) throws Exception{
        if(format!=null) {
            if(format.equals("xlsx")) {
                workspaceService.exportWorkspaces(response);
            }
            return new ResponseDTO("success",null,null);
        } else
            return new ResponseDTO("success",null,
                    workspaceService.listWorkspaces());
    }

    @RequestMapping(value="/{id}",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getWorkspace(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    workspaceService.getWorkspaceById(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/{id}/stacks",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getWorkspaceStacks(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    stackService.listStacksByWorkspaceId(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createAndSearchWorkspaces(@RequestBody WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        if(workspaceRequestDTO!=null&&workspaceRequestDTO.getSearch()!=null) {
            return new ResponseDTO("success", null,
                    workspaceService.searchWorkspaces(workspaceRequestDTO));
        } else if (workspaceRequestDTO!=null&&workspaceRequestDTO.getOwnerId()!=null&&
                workspaceRequestDTO.getName()!=null
                &&workspaceRequestDTO.getOrganizationId()!=null) {
            return new ResponseDTO("success",null,
                    workspaceService.createWorkspace(workspaceRequestDTO));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Object importWorkspace(WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        if(workspaceRequestDTO.getFile()!=null) {
            workspaceService.importWorkspaces(workspaceRequestDTO.getFile());
            return new ResponseDTO("success",null,null);
        }
        return new ResponseDTO("failure","File not present",null);
    }


    @RequestMapping(value="/{id}",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object updateWorkspace(@PathVariable Long id,@RequestBody WorkspaceRequestDTO workspaceRequestDTO)
            throws Exception{
        workspaceRequestDTO.setId(id);
        return new ResponseDTO("success",null,
                workspaceService.editWorkspace(workspaceRequestDTO));
    }

    @RequestMapping(value="/{id}",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteWorkspace(@PathVariable Long id) throws Exception{
        if (id!=null) {
            workspaceService.deleteWorkspace(id);
            return new ResponseDTO("success",null, null);
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganizationS(@RequestBody WorkspaceRequestDTO workspaceRequestDTO) throws Exception{
        if (workspaceRequestDTO.getIds()!=null) {
            return new ResponseDTO("success",null, workspaceService.deleteMultipleWorkspaces(workspaceRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }
}
