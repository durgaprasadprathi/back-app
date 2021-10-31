package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dto.*;
import com.appmodz.executionmodule.model.User;
import com.appmodz.executionmodule.service.UserService;
import com.appmodz.executionmodule.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController("v1UserController")
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UtilService utilService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    @ResponseBody
    public Object getUsers(@RequestParam(required = false) String format, HttpServletResponse response) throws Exception{
        if(format!=null) {
            if(format.equals("xlsx")) {
                userService.exportUsers(response);
            }
            return new ResponseDTO("success",null,null);
        } else
            return new ResponseDTO("success",null,
                    userService.listUsers());
    }

    @RequestMapping(value="/{id}",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getUser(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    userService.getUserById(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createAndSearchOrganizations(@RequestBody UserRequestDTO userRequestDTO) throws Exception{
        if(userRequestDTO!=null&&userRequestDTO.getSearch()!=null) {
            return new ResponseDTO("success", null,
                    userService.searchUsers(userRequestDTO));
        } else if (userRequestDTO!=null&&userRequestDTO.getUserName()!=null&& userRequestDTO.getPassword()!=null
                &&userRequestDTO.getOrganizationId()!=null&userRequestDTO.getRoleId()!=null) {
            return new ResponseDTO("success",null,userService.createUser(userRequestDTO));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Object importWorkspace(UserRequestDTO userRequestDTO) throws Exception{
        if(userRequestDTO.getFile()!=null) {
            userService.importUsers(userRequestDTO.getFile());
            return new ResponseDTO("success",null,null);
        }
        return new ResponseDTO("failure","File not present",null);
    }

    @RequestMapping(value="/{id}",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object updateUser(@PathVariable Long id,@RequestBody UserRequestDTO userRequestDTO) throws Exception{
            userRequestDTO.setId(id);
            return new ResponseDTO("success",null,
                    userService.updateUser(userRequestDTO));
    }

    @RequestMapping(value="/{id}",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteUser(@PathVariable Long id) throws Exception{
        if (id!=null) {
            userService.deleteUser(id);
            return new ResponseDTO("success",null, null);
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganizationS(@RequestBody UserRequestDTO userRequestDTO) throws Exception{
        if (userRequestDTO.getIds()!=null) {
            return new ResponseDTO("success",null, userService.deleteMultipleUsers(userRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }
}
