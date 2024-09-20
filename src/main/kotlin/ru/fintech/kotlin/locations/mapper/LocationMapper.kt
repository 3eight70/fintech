package ru.fintech.kotlin.locations.mapper

import ru.fintech.kotlin.locations.dto.LocationDto
import ru.fintech.kotlin.locations.entity.Location

object LocationMapper {
     fun entityToDto(entity: Location): LocationDto {
        return LocationDto(
            id = entity.id,
            slug = entity.slug,
            name = entity.name
        )
    }
}