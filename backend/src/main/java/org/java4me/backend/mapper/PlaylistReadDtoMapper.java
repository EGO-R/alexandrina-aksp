package org.java4me.backend.mapper;

import org.java4me.backend.database.entity.Playlist;
import org.java4me.backend.dto.PlaylistReadDto;
import org.springframework.stereotype.Component;

@Component
public class PlaylistReadDtoMapper implements Mapper<Playlist, PlaylistReadDto> {
    @Override
    public PlaylistReadDto map(Playlist obj) {
        return PlaylistReadDto.builder()
                .id(obj.getId())
                .name(obj.getName())
                .build();
    }
}
