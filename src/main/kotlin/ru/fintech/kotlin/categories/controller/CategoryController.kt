package ru.fintech.kotlin.categories.controller

import org.springframework.web.bind.annotation.*
import ru.fintech.kotlin.categories.CategoryService
import ru.fintech.kotlin.categories.dto.CategoryDto
import ru.fintech.kotlin.categories.dto.RequestCategoryDto

@RestController
@RequestMapping("/api/v1/places/categories")
class CategoryController(private val categoryService: CategoryService) {
    @GetMapping("/{id}")
    fun getCategory(@PathVariable("id") categoryId: Long): CategoryDto? {
        return categoryService.getCategoryById(categoryId)
    }

    @GetMapping
    fun getCategories(): List<CategoryDto> {
        return categoryService.getCategories()
    }

    @PostMapping
    fun createCategory(@RequestBody categoryDto: RequestCategoryDto): CategoryDto {
        return categoryService.createCategory(categoryDto)
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable("id") categoryId: Long,
        @RequestBody categoryDto: RequestCategoryDto
    ): CategoryDto {
        return categoryService.updateCategory(categoryId, categoryDto)
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable("id") categoryId: Long) {
        return categoryService.deleteCategory(categoryId)
    }
}