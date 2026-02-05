package com.gaming.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class GameDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private LocalDate releaseDate;
    private String publisher;
    private Double rating;
    private LocalDateTime createdAt;
    private Set<GenreDTO> genres;
    private Set<PlatformDTO> platforms;
}
