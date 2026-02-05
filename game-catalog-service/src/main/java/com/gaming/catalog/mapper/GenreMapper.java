package com.gaming.catalog.mapper;

import com.gaming.catalog.dto.GenreDTO;
import com.gaming.catalog.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDTO toDto(Genre genre);
    Genre toEntity(GenreDTO genreDTO);
}
