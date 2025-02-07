package com.maxiflexy.tickethelpdeskapp.controllers;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.RolesResponse;
import com.maxiflexy.tickethelpdeskapp.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/v1/ticket-helpdesk")
@AllArgsConstructor
public class RolesController {

    public final RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<RolesResponse>>> fetchRoles() {
        return ResponseEntity.ok()
                .body(roleService.fetchRoles());
    }
}
