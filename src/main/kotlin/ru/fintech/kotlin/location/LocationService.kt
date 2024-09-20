package ru.fintech.kotlin.location

import ru.fintech.kotlin.location.dto.LocationDto

interface LocationService {
    fun getLocations(): List<LocationDto>
    fun getLocation(id: Long): LocationDto?
    fun createLocation(locationDto: LocationDto): LocationDto
    fun updateLocation(id: Long, locationDto: LocationDto): LocationDto
    fun deleteLocation(id: Long)
}