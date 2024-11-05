package org.java4me.backend.http.controller;

import lombok.RequiredArgsConstructor;
import org.java4me.backend.dto.PlaylistCreateEditDto;
import org.java4me.backend.service.PlaylistService;
import org.java4me.backend.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private final VideoService videoService;

    @GetMapping
    public String getAllPlaylists(Model model) {
        model.addAttribute("playlists", playlistService.findAll());
        return "playlist/playlists";
    }

    @GetMapping("/{id}")
    public String getPlaylist(Model model,
                              @PathVariable("id") Integer id) {
        playlistService.findById(id)
                        .map(playlist ->
                                model.addAttribute("playlist", playlist))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("videos", videoService.findVideosInPlaylist(id));

        return "playlist/playlist";
    }

    @GetMapping("/create")
    public String createForm() {
        return "playlist/new_playlist";
    }

    @PostMapping("/create")
    public String create(@Validated PlaylistCreateEditDto playlist) {
        return "redirect:/playlists/" + playlistService.create(playlist).getId();
    }

    @GetMapping("/{id}/edit")
    public String getEdit(@PathVariable("id") Integer id,
                          Model model) {
        return playlistService.findById(id)
                .map(entity -> model.addAttribute("playlist", entity))
                .map(it -> "playlist/edit_playlist")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @Validated PlaylistCreateEditDto playlist) {
        return playlistService.update(id, playlist)
                .map(it -> "redirect:/playlists/{id}/edit")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        if (!playlistService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return "redirect:/playlists";
    }

    // TODO: 22.09.2024 user can get another user's playlist
    // TODO: 22.09.2024 show to user not his playlists
    // TODO: 22.09.2024 user can add video to someone else's playlist]
    // TODO: 22.09.2024 aff filter
}
