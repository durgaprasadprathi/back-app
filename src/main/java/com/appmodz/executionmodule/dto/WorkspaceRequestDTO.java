package com.appmodz.executionmodule.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class WorkspaceRequestDTO extends SearchRequestDTO{
    MultipartFile file;
    List<Long> ids;
    Long id;
    Long organizationId;
    Long ownerId;
    String name;
}
