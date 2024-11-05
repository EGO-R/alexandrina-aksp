package org.java4me.backend.database.repository;

import org.java4me.backend.database.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    @Query("""
SELECT p
FROM Playlist p
WHERE p.id NOT IN (
  SELECT pv.playlist.id
  FROM PlaylistVideo pv
  WHERE pv.video.id = :videoId
)
""")
    List<Playlist> findExceptVideo(Long videoId);
}
