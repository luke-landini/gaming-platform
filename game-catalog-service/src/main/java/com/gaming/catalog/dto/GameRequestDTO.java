package com.gaming.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class GameRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private LocalDate releaseDate;

    private String publisher;

    private Double rating;
    
    private String imageUrl;

    private Set<Long> genreIds;

    private Set<Long> platformIds;
}
