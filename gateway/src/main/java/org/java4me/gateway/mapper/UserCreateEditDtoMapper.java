package org.java4me.gateway.mapper;

import lombok.RequiredArgsConstructor;
import org.java4me.gateway.database.entity.User;
import org.java4me.gateway.dto.UserCreateEditDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreateEditDtoMapper implements Mapper<UserCreateEditDto, User> {
    private final PasswordEncoder passwordEncoder;


    @Override
    public User map(UserCreateEditDto obj) {
        var user = new User();
        copy(obj, user);

        return user;
    }

    private void copy(UserCreateEditDto fromObj, User toObj) {
        toObj.setUsername(fromObj.getUsername());
        toObj.setRole(fromObj.getRole());

        Optional.ofNullable(fromObj.getRawPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(toObj::setPassword);
    }

    @Override
    public User mapObject(UserCreateEditDto fromObj, User toObj) {
        copy(fromObj, toObj);
        return toObj;
    }
}
