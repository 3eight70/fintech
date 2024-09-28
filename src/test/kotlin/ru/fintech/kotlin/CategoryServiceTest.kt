package ru.fintech.kotlin

import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import ru.fintech.kotlin.category.dto.RequestCategoryDto
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.category.impl.CategoryServiceImpl
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryServiceTest {
    private lateinit var categoryService: CategoryServiceImpl
    private lateinit var categoryRepository: CustomGenericRepository<Category>

    @BeforeEach
    fun setup() {
        categoryRepository = mock()
        categoryService = CategoryServiceImpl(categoryRepository)
    }

    @Test
    @DisplayName("Должен возвращать список категорий")
    fun shouldReturnCategories() {
        val categories = listOf(
            Category(id = 1, name = "Category1", slug = "category1"),
            Category(id = 2, name = "Category2", slug = "category2")
        )
        whenever(categoryRepository.findAll()).thenReturn(categories)

        val result = categoryService.getCategories()

        verify(categoryRepository, times(1)).findAll()
        assert(result.size == 2)
        assert(result[0].name == "Category1")
        assert(result[1].name == "Category2")
    }

    @Test
    @DisplayName("Должен возвращать категорию по id")
    fun shouldReturnCategoryById() {
        val category = Category(id = 1, name = "Category1", slug = "category1")
        whenever(categoryRepository.findById(1)).thenReturn(category)

        val result = categoryService.getCategoryById(1)

        verify(categoryRepository, times(1)).findById(1)
        assert(result != null)
        assert(result?.name == "Category1")
    }

    @Test
    @DisplayName("Должен возвращать null, если категория не найдена")
    fun shouldReturnNullWhenCategoryNotFound() {
        whenever(categoryRepository.findById(1)).thenReturn(null)

        val result = categoryService.getCategoryById(1)

        verify(categoryRepository, times(1)).findById(1)
        assert(result == null)
    }

    @Test
    @DisplayName("Должен создавать новую категорию")
    fun shouldCreateCategory() {
        val createDto = RequestCategoryDto(name = "new", slug = "new")
        val savedCategory = Category(id = 1, name = "new", slug = "new")
        whenever(categoryRepository.save(any())).thenReturn(savedCategory)

        val result = categoryService.createCategory(createDto)

        verify(categoryRepository, times(1)).save(any())
        assert(result.name == "new")
        assert(result.slug == "new")
    }

    @Test
    @DisplayName("Должен обновлять существующую категорию")
    fun shouldUpdateCategory() {
        val existingCategory = Category(id = 1, name = "old", slug = "old")
        val updateDto = RequestCategoryDto(name = "new", slug = "new")
        whenever(categoryRepository.findById(1)).thenReturn(existingCategory)

        val result = categoryService.updateCategory(1, updateDto)

        verify(categoryRepository, times(1)).findById(1)
        assert(result.name == "new")
        assert(result.slug == "new")
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если категория для обновления не найдена")
    fun shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        val updateDto = RequestCategoryDto(name = "new", slug = "new")
        whenever(categoryRepository.findById(1)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            categoryService.updateCategory(1, updateDto)
        }

        Assertions.assertEquals(exception.message, "Категории с id: 1 не существует")

    }

    @Test
    @DisplayName("Должен удалять категорию по id")
    fun shouldDeleteCategory() {
        categoryService.deleteCategory(1)

        verify(categoryRepository, times(1)).delete(1)
    }
}