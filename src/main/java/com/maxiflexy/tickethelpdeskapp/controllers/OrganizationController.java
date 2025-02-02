package com.maxiflexy.tickethelpdeskapp.controllers;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.request.CreateOrganizationRequestDto;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UpdateOrganizationRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.*;
import com.maxiflexy.tickethelpdeskapp.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.maxiflexy.tickethelpdeskapp.constants.AppConstant.successResponseCode;
import static com.maxiflexy.tickethelpdeskapp.constants.OrganizationConstant.*;

@RestController
@RequestMapping("/v1/ticket-helpdesk/organization")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class OrganizationController {


    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<CreateOrganizationResponse>> createOrganization(
            @RequestBody CreateOrganizationRequestDto requestDto) {
        CreateOrganizationResponse response = organizationService.createOrganization(requestDto);
        ApiResponse<CreateOrganizationResponse> apiResponse = ApiResponse.<CreateOrganizationResponse>builder()
                .responseCode(successResponseCode).responseMessage(ORGANIZATION_CREATED_SUCCESSFULLY).status(true)
                .data(response).build();
        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping({"/{id}", ""})
    public ResponseEntity<ApiResponse<?>> fetchOrganization(@PathVariable(value = "id", required = false) String id) {
        if (id != null && !id.isEmpty()) {
            FetchOrganizationResponse organization = organizationService.fetchOrganizationById(id);
            return ResponseEntity.ok( ApiResponse.<FetchOrganizationResponse>builder()
                            .responseMessage(SUCCESS).status(true)
                            .responseCode(successResponseCode)
                            .data(organization)
                    .build());
        } else {
            List<FetchOrganizationResponse> organizations = organizationService.fetchOrganizations();
            return ResponseEntity.ok(ApiResponse.<List<FetchOrganizationResponse>>builder()
                    .responseMessage(SUCCESS).status(true)
                    .responseCode(successResponseCode)
                    .data(organizations)
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateOrganizationResponse>> updateOrganization(
            @PathVariable("id") String organizationId,
            @RequestBody UpdateOrganizationRequest requestDto) {
        UpdateOrganizationResponse response = organizationService.updateOrganization(organizationId, requestDto);
        ApiResponse<UpdateOrganizationResponse> apiResponse = ApiResponse.<UpdateOrganizationResponse>builder()
                .responseCode(successResponseCode).responseMessage(SUCCESSFULLY_UPDATED)
                .status(true).data(response).build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DeactivationResponse>> deactivateOrganization(
            @PathVariable("id") String organizationId) {
        DeactivationResponse response = organizationService.deactivateOrganization(organizationId);
        ApiResponse<DeactivationResponse> apiResponse = ApiResponse.<DeactivationResponse>builder()
                .responseCode(successResponseCode).responseMessage(SUCCESSFULLY_DEACTIVATED)
                .status(true).data(response).build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ActivationResponse>> activateOrganization(
            @PathVariable("id") String organizationId) {
        ActivationResponse response = organizationService.activateOrganization(organizationId);
        ApiResponse<ActivationResponse> apiResponse = ApiResponse.<ActivationResponse>builder()
                .responseCode(successResponseCode).responseMessage(SUCCESSFULLY_ACTIVATED)
                .status(true).data(response).build();
        return ResponseEntity.ok(apiResponse);
    }

}
