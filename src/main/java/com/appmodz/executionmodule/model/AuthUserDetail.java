package com.appmodz.executionmodule.model;

import com.appmodz.executionmodule.dao.RolePermissionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AuthUserDetail extends User implements UserDetails {

    RolePermissionDAO rolePermissionDAO;

    public AuthUserDetail(User user, RolePermissionDAO rolePermissionDAO) {
        super(user);
        this.rolePermissionDAO = rolePermissionDAO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Role role = getUserRole();
        grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        System.out.println(role.getRoleId());
        RolePermissions rolePermissions = rolePermissionDAO.getByRoleId(role.getRoleId());
        List<Permission> permissions = rolePermissions.getPermissions();
        for (Permission permission: permissions) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getPermissionName()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return super.getUserPasswordHash();
    }

    @Override
    public String getUsername() {
        return super.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
