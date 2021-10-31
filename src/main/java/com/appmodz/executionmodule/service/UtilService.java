package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.RolePermissionDAO;
import com.appmodz.executionmodule.dao.UserDAO;
import com.appmodz.executionmodule.dto.PermissionDTO;
import com.appmodz.executionmodule.model.Permission;
import com.appmodz.executionmodule.model.RolePermissions;
import com.appmodz.executionmodule.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilService {

    @Autowired
    UserService userService;

    @Autowired
    RolePermissionDAO rolePermissionDAO;

    public Boolean checkPermission(PermissionDTO permissionDTO, String requestPermission) {
        try {
            User user = this.getAuthenticatedUser();
            RolePermissions rolePermissions = rolePermissionDAO.getByRoleId(user.getUserRole().getRoleId());
            List<Permission> permissions = rolePermissions.getPermissions();
            for (Permission permission: permissions) {
                if(permission.getPermissionName().equals(requestPermission)) {
                    if(permission.getPermissionScope().equals("ORG")) {
                        return user.getUserOrganization().getOrganizationId() == permissionDTO.getOrganizationId() ;
                    }
                    if(permission.getPermissionScope().equals("SELF")) {
                        return user.getUserId() == permissionDTO.getUserId();
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public User getAuthenticatedUser() throws Exception{
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.getUserByUsername(username);
    }

}
