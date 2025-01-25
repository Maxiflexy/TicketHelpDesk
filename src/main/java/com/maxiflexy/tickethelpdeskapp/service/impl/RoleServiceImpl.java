package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.infometics.helpdesk.constants.AppConstant;
import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.response.RolesResponse;
import com.infometics.helpdesk.model.Role;
import com.infometics.helpdesk.model.User;
import com.infometics.helpdesk.repository.RoleRepository;
import com.infometics.helpdesk.repository.UserRepository;
import com.infometics.helpdesk.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl extends UserHandler implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        super(userRepository, roleRepository);
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Role fetchRoleById(long id) {
        return super.fetchRoleById(id);
    }

    @Override
    public ApiResponse<List<RolesResponse>> fetchRoles() {
        User currentUser = fetchLoggedInUser();
        List<Role> roles = roleRepository.findAll();
        List<RolesResponse> rolesResponses = new ArrayList<>();

        if (currentUser.getRole().getRoleName().equals(com.infometics.helpdesk.constants.Role.ROLE_SUPER_ADMIN.name())) {
            for (Role role : roles) {
                RolesResponse rolesResponse = new RolesResponse();
                BeanUtils.copyProperties(role, rolesResponse);
                rolesResponses.add(rolesResponse);
            }
        } else {
            rolesResponses = roles.stream()
                    .filter(role -> {
                        var r = com.infometics.helpdesk.constants.Role.valueOf(role.getRoleName());
                        log.info(currentUser.toString());
                        String organization = currentUser.getOrganization().getOrgCode().equals(AppConstant.INFOMETICS_ORG_CODE) ? AppConstant.INFOMETICS_ORG_CODE : AppConstant.CLIENT_ENUM_ROLE;
                        return r.getOrganization().equals(AppConstant.ADMIN_ENUM_ROLE) || r.getOrganization().equals(organization);
                    })
                    .map(role -> {
                        RolesResponse rolesResponse = new RolesResponse();
                        BeanUtils.copyProperties(role, rolesResponse);
                        return rolesResponse;
                    }).collect(Collectors.toCollection(ArrayList::new));
        }

        return ApiResponse
                .<List<RolesResponse>>builder()
                .status(true)
                .responseMessage("Roles fetched successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(rolesResponses)
                .build();
    }
}
