package com.appmodz.executionmodule.dto;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.Builder
public class PermissionDTO {
    Long organizationId;
    Long userId;
    Long workspaceId;
}
