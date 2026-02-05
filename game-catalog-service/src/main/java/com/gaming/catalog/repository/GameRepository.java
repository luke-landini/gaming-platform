package com.gaming.catalog.repository;

import com.gaming.catalog.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g LEFT JOIN g.genres genre LEFT JOIN g.platforms platform " +
           "WHERE (CAST(:title AS string) IS NULL OR LOWER(g.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) " +
           "AND (CAST(:genre AS string) IS NULL OR LOWER(genre.name) = LOWER(CAST(:genre AS string))) " +
           "AND (CAST(:platform AS string) IS NULL OR LOWER(platform.name) = LOWER(CAST(:platform AS string)))")
    Page<Game> searchGames(@Param("title") String title,
                           @Param("genre") String genre,
                           @Param("platform") String platform,
                           Pageable pageable);
}
