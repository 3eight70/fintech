package ru.fintech.kotlin.location.controller

import org.springframework.web.bind.annotation.*
import ru.fintech.kotlin.location.LocationService
import ru.fintech.kotlin.location.dto.LocationDto
import ru.fintech.kotlin.utils.annotation.LogExecutionTime

@RestController
@RequestMapping("/api/v1/locations")
class LocationController(private val locationService: LocationService) {
    @GetMapping
    @LogExecutionTime
    fun getLocations(): List<LocationDto> {
        return locationService.getLocations()
    }

    @GetMapping("/{id}")
    fun getLocation(@PathVariable id: Long): LocationDto? {
        return locationService.getLocation(id)
    }

    @PostMapping
    fun createLocation(@RequestBody dto: LocationDto): LocationDto {
        return locationService.createLocation(dto)
    }

    @PutMapping("/{id}")
    fun updateLocation(@PathVariable id: Long, @RequestBody dto: LocationDto): LocationDto {
        return locationService.updateLocation(id, dto)
    }

    @DeleteMapping("/{id}")
    fun deleteLocation(@PathVariable id: Long) {
        return locationService.deleteLocation(id)
    }
}