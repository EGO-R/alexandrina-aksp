package org.java4me.backend.mapper;

import org.java4me.backend.database.entity.Playlist;
import org.java4me.backend.dto.PlaylistCreateEditDto;
import org.springframework.stereotype.Component;

@Component
public class PlaylistCreateEditDtoMapper implements Mapper<PlaylistCreateEditDto, Playlist> {

    @Override
    public Playlist map(PlaylistCreateEditDto obj) {
        var playlist = new Playlist();
        return copy(obj, playlist);
    }

    @Override
    public Playlist mapObject(PlaylistCreateEditDto fromObj, Playlist toObj) {
        return copy(fromObj, toObj);
    }

    private Playlist copy(PlaylistCreateEditDto fromObj, Playlist toObj) {
        toObj.setName(fromObj.getName());
        return toObj;
    }
}
