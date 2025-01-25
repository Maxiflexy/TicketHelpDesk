package com.maxiflexy.tickethelpdeskapp.constants;

public enum Role {
    ROLE_SUPER_ADMIN("superadmin"),
    ROLE_ADMIN(AppConstant.ADMIN_ENUM_ROLE),
    ROLE_SUPPORT_CLIENT(AppConstant.CLIENT_ENUM_ROLE),
    ROLE_SUPPORT_INFOMETICS(AppConstant.INFOMETICS_ORG_CODE),
    ROLE_INFOMETICS_USER(AppConstant.INFOMETICS_ORG_CODE),
    ROLE_CLIENT(AppConstant.CLIENT_ENUM_ROLE);

    private final String organization;

    Role(String role_type) {
        this.organization = role_type;
    }
    public String getOrganization() {
        return organization;
    }
}
