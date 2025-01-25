package com.maxiflexy.tickethelpdeskapp.dtos.response;

import lombok.Data;

@Data
public class UserResponse {

    private long id;
    private String email;
    private String org_name;
    private String role_name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean status;

}
