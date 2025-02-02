package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.RolesResponse;
import com.maxiflexy.tickethelpdeskapp.model.Role;

import java.util.List;

public interface RoleService {

   Role fetchRoleById(long id);

    ApiResponse<List<RolesResponse>> fetchRoles();
}
