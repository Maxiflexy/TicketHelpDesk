package com.maxiflexy.tickethelpdeskapp.utils;

import com.maxiflexy.tickethelpdeskapp.dtos.request.CreateOrganizationRequestDto;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UpdateOrganizationRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.CreateOrganizationResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.FetchOrganizationResponse;
import com.maxiflexy.tickethelpdeskapp.model.Organization;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class OrganizationUtils {

    private static final String email = SecurityContextHolder.getContext().getAuthentication().getName();


    public static Organization buildOrganization(CreateOrganizationRequestDto requestDto) {
        Organization organization = new Organization();
        organization.setOrgCode(requestDto.getOrgCode());
        organization.setOrgName(requestDto.getOrgName());
        organization.setCreatedBy(email);
        organization.setOrgStatus(true);
        organization.setCreatedDate(LocalDateTime.now());
        return organization;
    }


    public static CreateOrganizationResponse buildOrganizationResponse(Organization savedOrganization) {
        CreateOrganizationResponse response = new CreateOrganizationResponse();
        response.setOrgId(savedOrganization.getId());
        return response;
    }

    public static FetchOrganizationResponse convertToDto(Organization organization) {
        return new FetchOrganizationResponse(
                String.valueOf(organization.getId()),
                organization.getOrgName(),
                organization.getOrgCode(),
                organization.isOrgStatus() ? "ACTIVE" : "INACTIVE"
        );
    }

    public static void buildUpdateOrganization(UpdateOrganizationRequest requestDto, Organization organization) {
        organization.setOrgName(requestDto.getOrgName());
        organization.setOrgCode(requestDto.getOrgCode());
        organization.setUpdatedBy(email);
        organization.setUpdatedDate(LocalDateTime.now());
    }

}
