package com.appmodz.executionmodule.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class StackRequestDTO extends SearchRequestDTO{
    MultipartFile file;
    Long id;
    Long terraformBackendId;
    Long ownerId;
    Long workspaceId;
    String stackState;
    String name;
    String stackLocation;
    String awsAccessKey;
    String awsSecretAccessKey;
    String awsRegion;
    List<Long> ids;
}
