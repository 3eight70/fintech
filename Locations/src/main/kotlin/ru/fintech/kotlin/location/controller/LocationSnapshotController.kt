package ru.fintech.kotlin.location.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.fintech.kotlin.location.LocationSnapshotService

@RestController
@RequestMapping("/api/v1/locations/snapshots")
class LocationSnapshotController(
    private val locationSnapshotService: LocationSnapshotService
) {
    @GetMapping
    fun getLocationSnaphots(@RequestParam("locationId") locationId: Long) =
        ResponseEntity.ok(locationSnapshotService.getLocationSnapshotHistory(locationId))
}