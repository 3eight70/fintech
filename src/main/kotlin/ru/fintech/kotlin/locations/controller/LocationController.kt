package ru.fintech.kotlin.locations.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fintech.kotlin.locations.LocationService
import ru.fintech.kotlin.locations.dto.LocationDto

@RestController
@RequestMapping("/api/v1/locations")
class LocationController(private val locationService: LocationService) {
    @GetMapping
    fun getLocations(): List<LocationDto> {
        return locationService.getLocations()
    }

    @GetMapping("/{id}")
    fun getLocation(@PathVariable id: Long): LocationDto? {
        return locationService.getLocation(id)
    }

    @DeleteMapping("/{id}")
    fun deleteLocation(@PathVariable id: Long) {
        return locationService.deleteLocation(id)
    }
}