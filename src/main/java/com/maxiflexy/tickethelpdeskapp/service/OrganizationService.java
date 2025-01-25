package com.maxiflexy.tickethelpdeskapp.service;

import com.infometics.helpdesk.dtos.request.CreateOrganizationRequestDto;
import com.infometics.helpdesk.dtos.request.UpdateOrganizationRequest;
import com.infometics.helpdesk.dtos.response.*;
import com.infometics.helpdesk.model.Organization;

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
