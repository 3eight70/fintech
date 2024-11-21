package ru.fintech.kotlin.category.controller

import org.springframework.web.bind.annotation.*
import ru.fintech.kotlin.category.CategoryService
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto
import ru.fintech.kotlin.utils.annotation.LogExecutionTime

@LogExecutionTime
@RestController
@RequestMapping("/api/v1/places/categories")
class CategoryController(private val categoryService: CategoryService) {
    @GetMapping("/{id}")
    fun getCategory(@PathVariable("id") categoryId: Long): CategoryDto? =
        categoryService.getCategoryById(categoryId)


    @GetMapping
    fun getCategories(): List<CategoryDto> =
        categoryService.getCategories()


    @PostMapping
    fun createCategory(@RequestBody categoryDto: RequestCategoryDto): CategoryDto =
        categoryService.createCategory(categoryDto)


    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable("id") categoryId: Long,
        @RequestBody categoryDto: RequestCategoryDto
    ): CategoryDto =
        categoryService.updateCategory(categoryId, categoryDto)


    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable("id") categoryId: Long) =
        categoryService.deleteCategory(categoryId)
}