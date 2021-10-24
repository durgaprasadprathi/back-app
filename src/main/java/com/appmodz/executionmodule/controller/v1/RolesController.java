package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dao.PermissionDAO;
import com.appmodz.executionmodule.dao.RoleDAO;
import com.appmodz.executionmodule.dao.RolePermissionDAO;
import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.dto.RolesRequestDTO;

import com.appmodz.executionmodule.model.Permission;
import com.appmodz.executionmodule.model.Role;
import com.appmodz.executionmodule.model.RolePermissions;
import com.appmodz.executionmodule.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController("v1RolesController")
@RequestMapping("/v1/roles")
public class RolesController {

    @Autowired
    RoleService roleService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getStacks(@RequestBody(required = false) RolesRequestDTO rolesRequestDTO) {
        return new ResponseDTO("success",null,
                roleService.listRoles());
    }

    @RequestMapping(value="/{id}",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getRole(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    roleService.getRoleById(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object createAndSearchRoles(@RequestBody RolesRequestDTO rolesRequestDTO) throws Exception{
        if(rolesRequestDTO!=null&&rolesRequestDTO.getSearch()!=null) {
            return new ResponseDTO("success", null, roleService.searchRoles(rolesRequestDTO));
        } else if (rolesRequestDTO!=null&&rolesRequestDTO.getName()!=null&& rolesRequestDTO.getDescription()!=null) {
            return new ResponseDTO("success",null,roleService.createRole(rolesRequestDTO));
        } else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/{id}",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object updateUser(@PathVariable Long id,@RequestBody RolesRequestDTO rolesRequestDTO) throws Exception{
        return new ResponseDTO("success",null, roleService.updateRole(rolesRequestDTO));
    }

    @RequestMapping(value="/{id}",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteUser(@PathVariable Long id) throws Exception{
        if (id!=null) {
            roleService.deleteRole(id);
            return new ResponseDTO("success",null, null);
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/",method= RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public Object deleteOrganizationS(@RequestBody RolesRequestDTO rolesRequestDTO) throws Exception{
        if (rolesRequestDTO.getIds()!=null) {
            return new ResponseDTO("success",null, roleService.deleteMultipleRoles(rolesRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/{id}/permissions",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getRolePermissions(@PathVariable Long id) throws Exception{
        if (id!=null) {
            return new ResponseDTO("success", null,
                    roleService.getRolePermissionsByRoleId(id));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/{id}/permissions",method= RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Object editRolePermissions(@PathVariable Long id,@RequestBody RolesRequestDTO rolesRequestDTO) throws Exception{
        if (id!=null) {
            rolesRequestDTO.setId(id);
            return new ResponseDTO("success", null, roleService.editPermissionsOfRole(rolesRequestDTO));
        }
        else {
            return new ResponseDTO("failure","Required parameters not present",null);
        }
    }

    @RequestMapping(value="/permissions",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getPermissions() throws Exception{
        return new ResponseDTO("success",null,
                roleService.listPermissions());
    }
}
