package org.java4me.gateway.dto;

import lombok.Builder;
import lombok.Value;
import org.java4me.gateway.database.entity.Role;

@Value
@Builder
public class UserReadDto {

    Long id;

    String username;

    Role role;
}
