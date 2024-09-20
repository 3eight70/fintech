package ru.fintech.kotlin.location.mapper

import ru.fintech.kotlin.location.dto.LocationDto
import ru.fintech.kotlin.location.entity.Location
import ru.fintech.kotlin.utils.annotation.LogExecutionTime

object LocationMapper {
     fun entityToDto(entity: Location): LocationDto {
        return LocationDto(
            id = entity.id,
            slug = entity.slug,
            name = entity.name
        )
    }
}