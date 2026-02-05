package com.gaming.catalog.mapper;

import com.gaming.catalog.dto.GameDTO;
import com.gaming.catalog.dto.GameRequestDTO;
import com.gaming.catalog.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GenreMapper.class, PlatformMapper.class})
public interface GameMapper {

    GameDTO toDto(Game game);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    Game toEntity(GameRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    void updateEntity(GameRequestDTO requestDTO, @MappingTarget Game game);
}
