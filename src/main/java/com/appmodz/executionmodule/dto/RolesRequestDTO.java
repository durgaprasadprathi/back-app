package com.appmodz.executionmodule.dto;

import com.appmodz.executionmodule.model.Permission;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class RolesRequestDTO extends SearchRequestDTO{
    Long id;
    String name;
    String description;
    List<Permission> permissions;
    List<Long> ids;
}
