package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.PermissionDAO;
import com.appmodz.executionmodule.dao.RoleDAO;
import com.appmodz.executionmodule.dao.RolePermissionDAO;
import com.appmodz.executionmodule.dto.PermissionDTO;
import com.appmodz.executionmodule.dto.RolesRequestDTO;
import com.appmodz.executionmodule.dto.SearchRequestDTO;
import com.appmodz.executionmodule.dto.SearchResultDTO;
import com.appmodz.executionmodule.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    RoleDAO roleDAO;

    @Autowired
    RolePermissionDAO rolePermissionDAO;

    @Autowired
    PermissionDAO permissionDAO;

    @Autowired
    UtilService utilService;

    public Role getRoleById(long id) throws Exception{
        Role role = roleDAO.get(id);
        if(role==null)
            throw new Exception("No role with this role id exists");
        else if(!utilService.checkPermission( PermissionDTO.builder().build(), "GET_ROLE"))
            throw new Exception("GET ROLE ACTION NOT PERMITTED FOR THIS USER");
        return role;
    }


    public void deleteRole(Long id) throws Exception {
        Role role = roleDAO.get(id);
        if(!utilService.checkPermission(PermissionDTO.builder().build(),"DELETE_ROLE"))
            throw new Exception("DELETE ROLE ACTION NOT PERMITTED FOR THIS USER");
        roleDAO.delete(role);
    }

    public String deleteMultipleRoles(RolesRequestDTO rolesRequestDTO) throws Exception{
        List<Long> ids = rolesRequestDTO.getIds();
        StringBuilder exceptions = new StringBuilder();
        StringBuilder successes = new StringBuilder();
        for (long id: ids) {
            Role role = roleDAO.get(id);
            if(!utilService.checkPermission(PermissionDTO.builder().build(),"DELETE_ROLE"))
                exceptions.append("DELETE ROLE ACTION NOT PERMITTED FOR THIS USER FOR ID").append(id);
            try {
                roleDAO.delete(role);
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

    public Role createRole(RolesRequestDTO rolesRequestDTO) throws Exception{
        if(!utilService.checkPermission(PermissionDTO.builder().build(),"CREATE_ROLE"))
            throw new Exception("CREATE ROLE ACTION NOT PERMITTED FOR THIS USER");
        Role role = new Role();
        role.setRoleName(rolesRequestDTO.getName());
        role.setRoleDescription(rolesRequestDTO.getDescription());
        roleDAO.save(role);
        List<Permission> perms= new ArrayList<>();
        RolePermissions rolePermissions = new RolePermissions();
        rolePermissions.setPermissions(perms);
        rolePermissions.setRoleId(role);
        rolePermissionDAO.save(rolePermissions);
        return role;
    }

    public Role updateRole(RolesRequestDTO rolesRequestDTO) throws Exception{
        Role role = roleDAO.get(rolesRequestDTO.getId());
        if(!utilService.checkPermission(PermissionDTO.builder().build(),"UPDATE_ROLE"))
            throw new Exception("UPDATE ROLE ACTION NOT PERMITTED FOR THIS USER");
        if(rolesRequestDTO.getName() != null)
            role.setRoleName(rolesRequestDTO.getName());
        if(rolesRequestDTO.getDescription() != null)
            role.setRoleDescription(rolesRequestDTO.getDescription());
        roleDAO.save(role);
        return role;
    }

    public List listRoles() {
        List<Role> roles = roleDAO.getAll();
        roles = roles.stream().filter(u->utilService.checkPermission(PermissionDTO.builder().build(),
                "GET_ROLE")).collect(Collectors.toList());
        return roles;
    }

    public SearchResultDTO searchRoles(SearchRequestDTO searchRequestDTO) {
        SearchResultDTO roles = roleDAO.search(searchRequestDTO);
        roles.setData((List) roles.getData().stream()
                .filter(u->utilService.checkPermission(PermissionDTO.builder().build(),
                        "GET_ROLE")).collect(Collectors.toList()));
        return roles;
    }

    public RolePermissions getRolePermissionsByRoleId(long id) throws Exception{
        RolePermissions rolePermissions = rolePermissionDAO.getByRoleId(id);
        if(rolePermissions==null)
            throw new Exception("No role permissions with this role id exists");
        else if(!utilService.checkPermission( PermissionDTO.builder().build(), "GET_ROLE"))
            throw new Exception("GET ROLE ACTION NOT PERMITTED FOR THIS USER");
        return rolePermissions;
    }

    public RolePermissions editPermissionsOfRole(RolesRequestDTO rolesRequestDTO) throws Exception{
        RolePermissions rolePermissions = rolePermissionDAO.getByRoleId(rolesRequestDTO.getId());
        if(rolePermissions==null)
            throw new Exception("No role permissions with this role id exists");
        else if(!utilService.checkPermission( PermissionDTO.builder().build(), "UPDATE_ROLE"))
            throw new Exception("UPDATE ROLE ACTION NOT PERMITTED FOR THIS USER");
        rolePermissions.setPermissions(rolesRequestDTO.getPermissions());
        rolePermissionDAO.save(rolePermissions);
        return rolePermissions;
    }

    public List listPermissions() {
        List<Permission> permissions = permissionDAO.getAll();
        permissions = permissions.stream().filter(u->utilService.checkPermission(PermissionDTO.builder().build(),
                "GET_PERMISSION")).collect(Collectors.toList());
        return permissions;
    }

}
