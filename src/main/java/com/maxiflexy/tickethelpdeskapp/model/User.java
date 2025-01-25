package com.maxiflexy.tickethelpdeskapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "app_user")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    private Organization organization;

    @Column(name = "user_status")
    private boolean status;

    @Column(name = "is_first_login")
    private boolean isFirstLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        getRole().getPermissions()
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getPermissionName())));
        authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isStatus();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isStatus();
    }

    @Override
    public boolean isEnabled() {
        return isStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(status, user.status).append(firstName, user.firstName).append(lastName, user.lastName).append(email, user.email).append(username, user.username).append(phoneNumber, user.phoneNumber).append(role, user.role).append(organization, user.organization).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(firstName).append(lastName).append(email).append(username).append(phoneNumber).append(status).toHashCode();
    }
}
