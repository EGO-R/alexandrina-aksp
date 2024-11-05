package org.java4me.gateway.database.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
