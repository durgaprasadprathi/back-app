package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class FileOrFolder {
    String name;
    Boolean isFile;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List data;
}
