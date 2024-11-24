package org.java4me.gateway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.java4me.gateway.validation.CreateAction;
import org.springframework.web.multipart.MultipartFile;

@Value
@Builder
public class VideoCreateEditDto {
    @NotNull
    String name;


    @NotNull(groups = CreateAction.class)
    MultipartFile video;
}
