package org.java4me.gateway.mapper;

import org.java4me.gateway.database.entity.User;
import org.java4me.gateway.dto.UserReadDto;
import org.springframework.stereotype.Component;

@Component
public class UserReadDtoMapper implements Mapper<User, UserReadDto> {
    @Override
    public UserReadDto map(User obj) {
        return UserReadDto.builder()
                .id(obj.getId())
                .username(obj.getUsername())
                .role(obj.getRole())
                .build();
    }
}
