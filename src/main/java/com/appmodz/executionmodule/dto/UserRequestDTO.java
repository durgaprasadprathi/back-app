package com.appmodz.executionmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class UserRequestDTO extends SearchRequestDTO{
    Long id;
    String userName;
    String firstName;
    String lastName;
    String password;
    String countryCode;
    String phoneNumber;
    String emailId;
    String address1;
    String address2;
    String country;
    Long organizationId;
    Long roleId;
    List<Long> ids;
}
