package com.maxiflexy.tickethelpdeskapp.dtos.request;

import lombok.Data;

@Data
public class UserRequest {

    private String email;
    private long org_id;
    private long role_id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
