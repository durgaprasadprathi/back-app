package com.appmodz.executionmodule.model;

import java.io.Serializable;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AuthenticationResponse implements Serializable {

    private String jwt;

    private User user;

    private List<Permission> permissions;

}
