package org.java4me.backend.mapper;

import org.java4me.backend.database.entity.Video;
import org.java4me.backend.dto.VideoCreateEditDto;
import org.springframework.stereotype.Component;

@Component
public class VideoCreateEditDtoMapper implements Mapper<VideoCreateEditDto, Video> {
    @Override
    public Video map(VideoCreateEditDto obj) {
        var video = new Video();
        return copy(obj, video);
    }

    @Override
    public Video mapObject(VideoCreateEditDto fromObj, Video toObj) {
        return copy(fromObj, toObj);
    }

    private Video copy(VideoCreateEditDto fromObj, Video toObj) {
        toObj.setName(fromObj.getName());
        return toObj;
    }
}
