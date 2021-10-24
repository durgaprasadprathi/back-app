package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dao.RolePermissionDAO;
import com.appmodz.executionmodule.dao.UserDAO;
import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.model.AuthenticationRequest;
import com.appmodz.executionmodule.model.AuthenticationResponse;
import com.appmodz.executionmodule.model.RolePermissions;
import com.appmodz.executionmodule.model.User;
import com.appmodz.executionmodule.service.JWTUtilService;
import com.appmodz.executionmodule.service.UserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("v1AuthController")
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RolePermissionDAO rolePermissionDAO;

    @Autowired
    private JWTUtilService jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        User user = userDAO.getByUsername(authenticationRequest.getUsername());

        RolePermissions rolePermissions = rolePermissionDAO.getByRoleId(user.getUserRole().getRoleId());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUser(user);
        authenticationResponse.setPermissions(rolePermissions.getPermissions());

        return ResponseEntity.ok(new ResponseDTO("success", null, authenticationResponse));
    }
}
