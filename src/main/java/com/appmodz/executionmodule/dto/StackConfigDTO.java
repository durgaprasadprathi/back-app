package com.appmodz.executionmodule.dto;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class StackConfigDTO {
    String stackName;
    String projectName;
    String awsRegion;
    String awsAccessKey;
    String awsSecretKey;
}
