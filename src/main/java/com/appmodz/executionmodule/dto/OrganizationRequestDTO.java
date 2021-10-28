package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OrganizationRequestDTO extends SearchRequestDTO{
    String name;

    List<Long> ids;
}
