package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class CanvasComponent {
    long id;
    String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object children;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object properties;
}
