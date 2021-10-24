package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@lombok.Getter
@lombok.Setter
public class PulumiRequestDTO {
    String stackPath;
    Object draftState;

    @SneakyThrows
    @JsonIgnoreProperties
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
