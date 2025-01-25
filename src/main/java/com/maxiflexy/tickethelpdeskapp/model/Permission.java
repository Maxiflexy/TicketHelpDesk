package com.maxiflexy.tickethelpdeskapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Permission extends BaseEntity implements GrantedAuthority {

    @Column( name = "permission_name")
    private String permissionName;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;

    @Override
    public String getAuthority() {
        return getPermissionName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(permissionName, that.permissionName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(permissionName).append(getId()).toHashCode();
    }
}
