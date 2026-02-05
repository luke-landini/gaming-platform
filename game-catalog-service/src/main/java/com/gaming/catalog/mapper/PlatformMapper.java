package com.gaming.catalog.mapper;

import com.gaming.catalog.dto.PlatformDTO;
import com.gaming.catalog.entity.Platform;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatformMapper {
    PlatformDTO toDto(Platform platform);
    Platform toEntity(PlatformDTO platformDTO);
}
