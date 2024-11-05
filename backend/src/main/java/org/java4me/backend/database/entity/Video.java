package org.java4me.backend.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"playlistVideos", "videoTags"})
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<PlaylistVideo> playlistVideos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<VideoTag> videoTags = new ArrayList<>();
}
