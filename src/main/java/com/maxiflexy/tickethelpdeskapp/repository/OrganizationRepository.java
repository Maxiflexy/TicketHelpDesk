package com.maxiflexy.tickethelpdeskapp.repository;

import com.maxiflexy.tickethelpdeskapp.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByOrgName(String organizationName);

    boolean existsByOrgCode(String organizationCode);

    boolean existsByOrgNameAndIdNot(String orgName, Long orgId);

    boolean existsByOrgCodeAndIdNot(String orgCode, Long orgId);

}
