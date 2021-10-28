package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.RolePermissionDAO;
import com.appmodz.executionmodule.dao.UserDAO;
import com.appmodz.executionmodule.model.AuthUserDetail;
import com.appmodz.executionmodule.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user  = userDAO.getByUsername(name);
        if (user==null)
            throw new UsernameNotFoundException("Username or password wrong");
        UserDetails userDetails = new AuthUserDetail(user,rolePermissionDAO);
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
    }

}
