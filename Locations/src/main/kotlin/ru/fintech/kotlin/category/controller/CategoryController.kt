package ru.fintech.kotlin.category.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fintech.kotlin.category.CategoryService
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto
import ru.fintech.kotlin.utils.annotation.LogExecutionTime

@LogExecutionTime
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