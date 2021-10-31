package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SearchResultDTO {
    List data;
    Long total;
}
