package ru.fintech.kotlin.category.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.SnapshotEntity
import java.time.Instant

@SnapshotEntity(tableName = "category_snapshots")
class CategorySnapshot(
    override val id: Long,
    val name: String,
    val slug: String,
    val timestamp: Instant
) : IdentifiableEntity {
    constructor(category: Category) : this(
        id = category.id,
        slug = category.slug,
        name = category.name,
        timestamp = Instant.now()
    )
}