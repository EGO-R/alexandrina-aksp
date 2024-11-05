package org.java4me.backend.http.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.java4me.backend.dto.PageResponse;
import org.java4me.backend.dto.PlaylistVideoCreateEditDto;
import org.java4me.backend.dto.VideoCreateEditDto;
import org.java4me.backend.dto.VideoFilter;
import org.java4me.backend.service.PlaylistService;
import org.java4me.backend.service.VideoService;
import org.java4me.backend.validation.CreateAction;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final PlaylistService playlistService;

    @GetMapping
    public String getAllVideos(Model model,
                               VideoFilter filter,
                               Pageable pageable) {

        model.addAttribute("filter", filter);
        model.addAttribute("page", PageResponse.of(videoService.findAll(filter, pageable)));
        return "video/videos";
    }

    @GetMapping("/{id}")
    public String getVideoById(@PathVariable("id") Long id,
                               Model model) {
        var video = videoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("video", video);

        model.addAttribute("playlists", playlistService.findExceptVideo(video.getId()));

        return "video/video";
    }

    @GetMapping("/create")
    public String createForm() {
        return "video/new_video";
    }

    @PostMapping("/create")
    public String create(@Validated({Default.class, CreateAction.class}) VideoCreateEditDto video) {
        return "redirect:/videos/" + videoService.create(video).getId();
    }

    @PostMapping("/{id}/toPlaylist")
    public String toPlaylist(@RequestParam Integer playlistId,
                             @PathVariable("id") Long videoId) {
        videoService.toPlaylist(PlaylistVideoCreateEditDto.builder()
                .playlistId(playlistId)
                .videoId(videoId)
                .build());
        return "redirect:/videos/{id}";
    }

    @PostMapping("/{id}/fromPlaylist")
    public String fromPlaylist(@RequestParam Integer playlistId,
                             @PathVariable("id") Long videoId) {
        var playlistVideoCreateEditDto = PlaylistVideoCreateEditDto.builder()
                .playlistId(playlistId)
                .videoId(videoId)
                .build();
        if (!videoService.fromPlaylist(playlistVideoCreateEditDto))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return "redirect:/videos/{id}";
    }

    @GetMapping("/{id}/edit")
    public String getUpdate(@PathVariable("id") Long id,
                         Model model) {
        videoService.findById(id)
                .map(video -> model.addAttribute("video", video))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return "video/edit_video";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Validated VideoCreateEditDto video) {
        return videoService.update(video, id)
                .map(it -> "redirect:/videos/{id}/edit")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        if (!videoService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return "redirect:/videos";
    }
}
