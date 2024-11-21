package ru.fintech.kotlin.category.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.CategoryService
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.category.mapper.CategoryMapper
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import kotlin.random.Random

@Service
class CategoryServiceImpl(
    private val categoryRepository: CustomGenericRepository<Category> = CustomGenericRepository(
        Category::class,
        EntityScanner.getEntityStorage()
    )
) : CategoryService {
    private val log = LoggerFactory.getLogger(CategoryServiceImpl::class.java)

    override fun getCategories(): List<CategoryDto> {
        log.info("Получен запрос на получение категорий")
        return categoryRepository.findAll()
            .stream()
            .map(CategoryMapper::entityToDto)
            .toList()
    }

    override fun getCategoryById(id: Long): CategoryDto? {
        log.info("Получен запрос на получение категории с id: $id")
        val category = categoryRepository.findById(id)

        return if (category != null) {
            CategoryMapper.entityToDto(category)
        } else
            null
    }

    override fun createCategory(createDto: RequestCategoryDto): CategoryDto {
        log.info("Получен запрос на создание категории с name: ${createDto.name} и slug: ${createDto.slug}")
        val category = Category(
            id = Random.nextLong(),
            name = createDto.name,
            slug = createDto.slug
        )

        return CategoryMapper.entityToDto(categoryRepository.save(category))
    }

    override fun updateCategory(id: Long, updateDto: RequestCategoryDto): CategoryDto {
        log.info("Получен запрос на обновление категории с id: $id на $updateDto")
        val category =
            categoryRepository.findById(id) ?: throw IllegalArgumentException("Категории с id: $id не существует")

        category.name = updateDto.name
        category.slug = updateDto.slug

        return CategoryMapper.entityToDto(category)
    }

    override fun deleteCategory(id: Long) {
        log.info("Получен запрос на удаление категории с id: $id")
        categoryRepository.delete(id)
    }
}