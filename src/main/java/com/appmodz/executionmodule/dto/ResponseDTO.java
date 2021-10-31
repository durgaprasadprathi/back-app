package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.AllArgsConstructor
public class ResponseDTO {
    String status;

    String message;

    Object data;
}
