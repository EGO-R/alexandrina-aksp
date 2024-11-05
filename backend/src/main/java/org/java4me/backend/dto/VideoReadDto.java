package org.java4me.backend.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class VideoReadDto {
    Long id;

    String name;

    List<PlaylistReadDto> playlists;

//    List<String> tags;
}
