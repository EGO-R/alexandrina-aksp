package org.java4me.backend.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "id")
@Builder
public class PlaylistReadDto {

    Integer id;

    String name;
}
