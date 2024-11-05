package org.java4me.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.java4me.backend.database.entity.PlaylistVideo;
import org.java4me.backend.database.repository.PlaylistVideoRepository;
import org.java4me.backend.database.repository.SortType;
import org.java4me.backend.database.repository.VideoRepository;
import org.java4me.backend.dto.PlaylistVideoCreateEditDto;
import org.java4me.backend.dto.VideoCreateEditDto;
import org.java4me.backend.dto.VideoFilter;
import org.java4me.backend.dto.VideoReadDto;
import org.java4me.backend.mapper.PlaylistVideoCreateEditDtoMapper;
import org.java4me.backend.mapper.VideoCreateEditDtoMapper;
import org.java4me.backend.mapper.VideoReadDtoMapper;
import org.java4me.backend.querydsl.QPredicates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.java4me.backend.database.entity.QVideo.video;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoReadDtoMapper videoReadDtoMapper;
    private final VideoCreateEditDtoMapper videoCreateEditDtoMapper;
    private final PlaylistVideoCreateEditDtoMapper playlistVideoCreateEditDtoMapper;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final VideoStorageService videoStorageService;

    @Transactional
    public void toPlaylist(PlaylistVideoCreateEditDto playlistVideoCreateEditDto) {
        var playlistVideo = playlistVideoCreateEditDtoMapper.map(playlistVideoCreateEditDto);

        playlistVideoRepository.saveAndFlush(playlistVideo);
    }

    @Transactional
    public boolean fromPlaylist(PlaylistVideoCreateEditDto playlistVideoCreateEditDto) {
        return playlistVideoRepository.findByPlaylistIdAndVideoId(
                        playlistVideoCreateEditDto.getPlaylistId(),
                        playlistVideoCreateEditDto.getVideoId())
                        .map(entity -> {
                            playlistVideoRepository.delete(entity);
                            return true;
                        }) .orElse(false);

    }

    public List<VideoReadDto> findVideosInPlaylist(Integer playlistId) {
        return findVideosInPlaylistSorted(playlistId, SortType.DESC);
    }

    public List<VideoReadDto> findVideosInPlaylistSorted(Integer playlistId, SortType sortType) {
        var typedSort = Sort.sort(PlaylistVideo.class).by(PlaylistVideo::getModifiedAt);
        Sort sort;

        if (sortType == SortType.ASC)
            sort = typedSort.ascending();
        else
            sort = typedSort.descending();

        return videoRepository.findByPlaylist(playlistId, sort).stream()
                .map(videoReadDtoMapper::map)
                .toList();
    }


    public Page<VideoReadDto> findAll(VideoFilter filter, Pageable pageable) {
        var predicate = QPredicates.builder()
                .add(filter.getName(), video.name::containsIgnoreCase)
                .build();

        return videoRepository.findAll(predicate, pageable)
                .map(videoReadDtoMapper::map);
    }


    public List<VideoReadDto> findAll() {
        return videoRepository.findAll().stream()
                .map(videoReadDtoMapper::map)
                .toList();
    }

    public Optional<VideoReadDto> findById(Long id) {
        return videoRepository.findById(id)
                .map(videoReadDtoMapper::map);
    }

    @Transactional
    public VideoReadDto create(VideoCreateEditDto videoCreateEditDto) {
        return Optional.of(videoCreateEditDto)
                .map(videoCreateEditDtoMapper::map)
                .map(videoRepository::saveAndFlush)
                .map(entity -> {
                    uploadVideo(entity.getId().toString(), videoCreateEditDto.getVideo());
                    return videoReadDtoMapper.map(entity);
                })
                .orElseThrow();
    }

    @SneakyThrows
    private void uploadVideo(String name, MultipartFile video) {
        videoStorageService.upload(name, video.getInputStream());
    }

    @Transactional
    public Optional<VideoReadDto> update(VideoCreateEditDto videoCreateEditDto, Long id) {
        return videoRepository.findById(id)
                .map(video -> videoCreateEditDtoMapper.mapObject(videoCreateEditDto, video))
                .map(videoRepository::saveAndFlush)
                .map(videoReadDtoMapper::map);
    }

    @Transactional
    public boolean delete(Long id) {
        return videoRepository.findById(id)
                .map(entity -> {
                    videoRepository.delete(entity);
                    videoStorageService.delete(id.toString());
                    return true;
                })
                .orElse(false);
    }
}
