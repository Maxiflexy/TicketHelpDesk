package com.maxiflexy.tickethelpdeskapp.service;

import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.response.RolesResponse;
import com.infometics.helpdesk.model.Role;

import java.util.List;

public interface RoleService {

   Role fetchRoleById(long id);

    ApiResponse<List<RolesResponse>> fetchRoles();
}
