package ru.fintech.kotlin.category.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.fintech.kotlin.category.CategorySnapshotService

@RestController
@RequestMapping("/api/v1/places/categories/snapshots")
class CategorySnapshotController(
    private val categorySnapshotService: CategorySnapshotService
) {
    @GetMapping
    fun getCategorySnaphots(@RequestParam("categoryId") categoryId: Long) =
        ResponseEntity.ok(categorySnapshotService.getCategorySnapshotHistory(categoryId))
}