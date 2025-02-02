package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.dtos.request.CreateOrganizationRequestDto;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UpdateOrganizationRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.*;
import com.maxiflexy.tickethelpdeskapp.model.Organization;

import java.util.List;

public interface OrganizationService {

    Organization getOrganizationById(long id);

    CreateOrganizationResponse createOrganization(CreateOrganizationRequestDto requestDto);

    List<FetchOrganizationResponse> fetchOrganizations();

    FetchOrganizationResponse fetchOrganizationById(String id);

    UpdateOrganizationResponse updateOrganization(String organizationId, UpdateOrganizationRequest requestDto);

    DeactivationResponse deactivateOrganization(String organizationId);

    ActivationResponse activateOrganization(String organizationId);
}
