package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SearchRequestDTO {
    private String search;

    private Integer pageNo;

    private Integer itemPerPage;

    @JsonProperty("sort")
    private Sort sort;

    public SearchRequestDTO() {
        this.sort = new Sort();
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.ToString
    public static class Sort {
        @JsonProperty("attribute")
        private String attribute;

        @JsonProperty("sort")
        private String sort;

        @JsonSetter("attribute")
        public void setAttribute(String s) {
            this.attribute = Objects.requireNonNullElse(s, "id");
        }

        @JsonSetter("sort")
        public void setSort(String s) {
            this.sort = Objects.requireNonNullElse(s, "desc");
        }

        public Sort() {
            this.attribute = "id";
            this.sort = "desc";
        }
    }
}
