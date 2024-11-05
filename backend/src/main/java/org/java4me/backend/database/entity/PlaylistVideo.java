package org.java4me.backend.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(of = {"playlist", "video"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistVideo extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;


    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        playlist.getPlaylistVideos().add(this);
    }

    public void setVideo(Video video) {
        this.video = video;
        video.getPlaylistVideos().add(this);
    }
}
