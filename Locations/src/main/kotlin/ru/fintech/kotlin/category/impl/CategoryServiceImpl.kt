package ru.fintech.kotlin.category.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.CategoryService
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.category.entity.CategorySnapshot
import ru.fintech.kotlin.category.mapper.CategoryMapper
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.SnapshotScanner
import ru.fintech.kotlin.datasource.observer.GenericObserver
import ru.fintech.kotlin.datasource.observer.Subject
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.datasource.repository.impl.CustomSnapshotRepository
import kotlin.random.Random

@Service
class CategoryServiceImpl(
    private val categoryRepository: CustomGenericRepository<Category> = CustomGenericRepository(
        Category::class,
        EntityScanner.getEntityStorage()
    ),
    private val categorySnapshotRepository: CustomSnapshotRepository<CategorySnapshot> = CustomSnapshotRepository(
        CategorySnapshot::class,
        SnapshotScanner.getEntityStorage()
    )
) : CategoryService {
    private val log = LoggerFactory.getLogger(CategoryServiceImpl::class.java)
    private val subject = Subject<Category>()
    private val observer = GenericObserver(categoryRepository)

    init {
        subject.attach(observer)
    }

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
        categorySnapshotRepository.save(CategorySnapshot(category))
        subject.notifyObservers(category)

        return CategoryMapper.entityToDto(category)
    }

    override fun updateCategory(id: Long, updateDto: RequestCategoryDto): CategoryDto {
        log.info("Получен запрос на обновление категории с id: $id на $updateDto")
        val category =
            categoryRepository.findById(id) ?: throw IllegalArgumentException("Категории с id: $id не существует")

        category.name = updateDto.name
        category.slug = updateDto.slug
        categorySnapshotRepository.save(CategorySnapshot(category))

        return CategoryMapper.entityToDto(category)
    }

    override fun deleteCategory(id: Long) {
        log.info("Получен запрос на удаление категории с id: $id")
        categoryRepository.delete(id)
    }
}