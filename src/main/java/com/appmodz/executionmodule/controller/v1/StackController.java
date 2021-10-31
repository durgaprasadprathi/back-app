package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.dto.StackRequestDTO;
import com.appmodz.executionmodule.service.StackService;
import com.appmodz.executionmodule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("v1StackController")
@RequestMapping("/v1/stacks")
public class StackController {
    @Autowired
    StackService stackService;

    @Autowired
    UserService userService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    @ResponseBody
    public Object getStacks(@RequestParam(required = false) String format, HttpServletResponse response) throws Exception{
        if(format!=null) {
            if(format.equals("xlsx")) {
                stackService.exportStacks(response);
            }
            return new ResponseDTO("success",null,null);
        } else
            return new ResponseDTO("success",null,
                    stackService.listStacks());
    }


    @RequestMapping(value="/{id}",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getUser(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    stackService.getStackById(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createAndSearchOrganizations(@RequestBody StackRequestDTO stackRequestDTO) throws Exception{
        if(stackRequestDTO!=null&&stackRequestDTO.getSearch()!=null) {
            return new ResponseDTO("success", null,
                    stackService.searchStacks(stackRequestDTO));
        } else if (stackRequestDTO!=null&&stackRequestDTO.getOwnerId()!=null&& stackRequestDTO.getName()!=null
                &&stackRequestDTO.getWorkspaceId() !=null&&stackRequestDTO.getAwsAccessKey()!=null
                &&stackRequestDTO.getAwsRegion()!=null&&stackRequestDTO.getAwsSecretAccessKey()!=null) {
            return new ResponseDTO("success",null,stackService.createStack(stackRequestDTO));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Object importWorkspace(StackRequestDTO stackRequestDTO) throws Exception{
        if(stackRequestDTO.getFile()!=null) {
            stackService.importStacks(stackRequestDTO.getFile());
            return new ResponseDTO("success",null,null);
        }
        return new ResponseDTO("failure","File not present",null);
    }


    @RequestMapping(value="/{id}",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object updateUser(@PathVariable Long id,@RequestBody StackRequestDTO stackRequestDTO) throws Exception{
        stackRequestDTO.setId(id);
        return new ResponseDTO("success",null,
                stackService.editStack(stackRequestDTO));
    }

    @RequestMapping(value="/{id}",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganization(@PathVariable Long id) throws Exception{
        if (id!=null) {
            stackService.deleteStack(id);
            return new ResponseDTO("success",null, null);
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganizationS(@RequestBody StackRequestDTO stackRequestDTO) throws Exception{
        if (stackRequestDTO.getIds()!=null) {
            return new ResponseDTO("success",null, stackService.deleteMultipleStacks(stackRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }
}
