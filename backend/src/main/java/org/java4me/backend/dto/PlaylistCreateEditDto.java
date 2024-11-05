package org.java4me.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaylistCreateEditDto {
    @NotNull
    String name;

}
