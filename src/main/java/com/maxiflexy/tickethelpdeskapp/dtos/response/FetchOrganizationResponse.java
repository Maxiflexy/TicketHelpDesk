package com.maxiflexy.tickethelpdeskapp.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FetchOrganizationResponse {

    private String orgId;
    private String orgName;
    private String orgCode;
    private String orgStatus;



}
