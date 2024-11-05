package org.java4me.gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.java4me.gateway.database.entity.Role;
import org.java4me.gateway.validation.CreateAction;

@Value

public class UserCreateEditDto {

    @Email
    @NotNull
    String username;

    @NotNull(groups = {CreateAction.class})
    String rawPassword;

    @NotNull
    Role role;
}
