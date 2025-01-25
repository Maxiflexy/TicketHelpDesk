package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.infometics.helpdesk.dtos.request.CreateOrganizationRequestDto;
import com.infometics.helpdesk.dtos.request.UpdateOrganizationRequest;
import com.infometics.helpdesk.dtos.response.*;
import com.infometics.helpdesk.exceptions.BadRequestException;
import com.infometics.helpdesk.exceptions.DuplicateResourceException;
import com.infometics.helpdesk.exceptions.NotFoundException;
import com.infometics.helpdesk.model.Organization;
import com.infometics.helpdesk.repository.OrganizationRepository;
import com.infometics.helpdesk.service.OrganizationService;
import com.infometics.helpdesk.utils.OrganizationUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.infometics.helpdesk.constants.OrganizationConstant.*;
import static com.infometics.helpdesk.constants.OrganizationConstant.ORGANIZATION_CODE_ALREADY_EXISTS;
import static com.infometics.helpdesk.utils.OrganizationUtils.*;

@AllArgsConstructor
@Service
public class OrganizationImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;


    @Override
    public CreateOrganizationResponse createOrganization(CreateOrganizationRequestDto requestDto) {
        validateOrganizationDoesNotExist(requestDto);
        Organization organization = buildOrganization(requestDto);
        Organization savedOrganization = organizationRepository.save(organization);
        return buildOrganizationResponse(savedOrganization);
    }


    @Override
    public FetchOrganizationResponse fetchOrganizationById(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) throw new BadRequestException(ORGANIZATION_ID_CANNOT_BE_NULL_OR_EMPTY);
        long orgId;
        try {
            orgId = Long.parseLong(organizationId);
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_ID_FORMAT);
        }
        return organizationRepository.findById(orgId)
                .map(OrganizationUtils::convertToDto)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND));
    }



    @Override
    public List<FetchOrganizationResponse> fetchOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(OrganizationUtils::convertToDto)
                .collect(Collectors.toList());
    }



    @Override
    public UpdateOrganizationResponse updateOrganization(String organizationId, UpdateOrganizationRequest requestDto) {
        if (organizationId == null || organizationId.isBlank()) throw new BadRequestException(ORGANIZATION_ID_CANNOT_BE_NULL_OR_EMPTY);
        Organization organization = organizationRepository.findById(Long.valueOf(organizationId))
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND));
        validateOrgNameAndCodeDoesNotExist(organizationId, requestDto);
        buildUpdateOrganization(requestDto, organization);
        Organization updatedOrganization = organizationRepository.save(organization);
        UpdateOrganizationResponse response = new UpdateOrganizationResponse();
        response.setOrgId(String.valueOf(updatedOrganization.getId()));
        return response;
    }



    @Override
    public DeactivationResponse deactivateOrganization(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) throw new BadRequestException(ORGANIZATION_ID_CANNOT_BE_NULL_OR_EMPTY);
        Organization organization = organizationRepository.findById(Long.valueOf(organizationId))
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND));
        if (!organization.isOrgStatus()) throw new BadRequestException(ORGANIZATION_IS_ALREADY_INACTIVE);
        organization.setOrgStatus(false);
        organizationRepository.save(organization);
        DeactivationResponse response = new DeactivationResponse();
        response.setOrgId(String.valueOf(organization.getId()));
        return response;
    }


    @Override
    public ActivationResponse activateOrganization(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) throw new BadRequestException(ORGANIZATION_ID_CANNOT_BE_NULL_OR_EMPTY);
        Organization organization = organizationRepository.findById(Long.valueOf(organizationId))
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND));
        if (organization.isOrgStatus()) throw new BadRequestException(ORGANIZATION_IS_ALREADY_ACTIVE);
        organization.setOrgStatus(true);
        organizationRepository.save(organization);
        ActivationResponse response = new ActivationResponse();
        response.setOrgId(String.valueOf(organization.getId()));
        return response;
    }


    private void validateOrganizationDoesNotExist(CreateOrganizationRequestDto requestDto) {
        String organizationName = requestDto.getOrgName();
        String organizationCode = requestDto.getOrgCode();

        if (organizationName == null || organizationName.trim().isEmpty()) {
            throw new BadRequestException(ORGANIZATION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (organizationCode == null || organizationCode.trim().isEmpty()) {
            throw new BadRequestException(ORGANIZATION_CODE_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (organizationRepository.existsByOrgName(organizationName) ||
                organizationRepository.existsByOrgCode(organizationCode)) {
            throw new DuplicateResourceException(ORGANIZATION_ALREADY_EXISTS);
        }
    }



    private void validateOrgNameAndCodeDoesNotExist(String organizationId, UpdateOrganizationRequest requestDto) {
        if (organizationRepository.existsByOrgNameAndIdNot(requestDto.getOrgName(), Long.valueOf(organizationId))) {
            throw new BadRequestException(ORGANIZATION_NAME_ALREADY_EXISTS);
        }

        if (organizationRepository.existsByOrgCodeAndIdNot(requestDto.getOrgCode(), Long.valueOf(organizationId))) {
            throw new BadRequestException(ORGANIZATION_CODE_ALREADY_EXISTS);
        }
    }


    @Override
    public Organization getOrganizationById(long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isPresent()) {
            return organization.get();
        }
        throw new EntityNotFoundException("Organization "+ id + " not found");
    }
}
