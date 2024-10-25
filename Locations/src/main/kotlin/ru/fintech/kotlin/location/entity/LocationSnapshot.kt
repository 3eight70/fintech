package ru.fintech.kotlin.location.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.SnapshotEntity
import java.time.Instant

@SnapshotEntity(tableName = "location_snapshots")
class LocationSnapshot(
    override val id: Long,
    val name: String,
    val slug: String,
    val timestamp: Instant
) : IdentifiableEntity {
    constructor(location: Location) : this(
        id = location.id,
        slug = location.slug,
        name = location.name,
        timestamp = Instant.now()
    )
}