package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OrganizationRequestDTO extends SearchRequestDTO{
    String name;
    MultipartFile file;
    List<Long> ids;
}
