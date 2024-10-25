package ru.fintech.kotlin.location

import org.springframework.stereotype.Service
import ru.fintech.kotlin.datasource.SnapshotScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomSnapshotRepository
import ru.fintech.kotlin.location.entity.LocationSnapshot


interface LocationSnapshotService {
    fun getLocationSnapshotHistory(id: Long): List<LocationSnapshot>
}

@Service
class LocationSnapshotServiceImpl(
    private val locationSnapshotRepository: CustomSnapshotRepository<LocationSnapshot> = CustomSnapshotRepository(
        LocationSnapshot::class,
        SnapshotScanner.getEntityStorage()
    )
) : LocationSnapshotService {
    override fun getLocationSnapshotHistory(id: Long): List<LocationSnapshot> =
        locationSnapshotRepository.findAllById(id)
}