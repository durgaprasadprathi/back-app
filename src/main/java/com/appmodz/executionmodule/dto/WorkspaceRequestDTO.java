package com.appmodz.executionmodule.dto;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class WorkspaceRequestDTO extends SearchRequestDTO{
    List<Long> ids;
    Long id;
    Long organizationId;
    Long ownerId;
    String name;
}
