package ru.fintech.kotlin

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.fintech.kotlin.category.CategoryService
import ru.fintech.kotlin.category.controller.CategoryController
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto

@WebMvcTest(CategoryController::class)
class CategoryControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var categoryService: CategoryService

    private lateinit var categoryDto: CategoryDto
    private lateinit var requestCategoryDto: RequestCategoryDto
    private val testCategory = "test"
    private val id: Long = 123

    @BeforeEach
    fun setUp() {
        categoryDto = CategoryDto(id = id, name = testCategory, slug = testCategory)
        requestCategoryDto = RequestCategoryDto(name = testCategory, slug = testCategory)
    }

    @Test
    @DisplayName("Должен вернуть категорию по id")
    fun shouldReturnCategoryById() {
        whenever(categoryService.getCategoryById(id)).thenReturn(categoryDto)

        mockMvc.perform(get("/api/v1/places/categories/$id"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value(testCategory))
            .andExpect(jsonPath("$.slug").value(testCategory))

        verify(categoryService, times(1)).getCategoryById(id)
    }

    @Test
    @DisplayName("Должен вернуть все категории")
    fun shouldReturnAllCategories() {
        whenever(categoryService.getCategories()).thenReturn(listOf(categoryDto))

        mockMvc.perform(get("/api/v1/places/categories"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(id))
            .andExpect(jsonPath("$[0].name").value(testCategory))
            .andExpect(jsonPath("$[0].slug").value(testCategory))

        verify(categoryService, times(1)).getCategories()
    }

    @Test
    @DisplayName("Должен создать новую категорию")
    fun shouldCreateNewCategory() {
        whenever(categoryService.createCategory(any<RequestCategoryDto>())).thenReturn(categoryDto)

        mockMvc.perform(
            post("/api/v1/places/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCategoryDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value(testCategory))
            .andExpect(jsonPath("$.slug").value(testCategory))

        verify(categoryService, times(1)).createCategory(any<RequestCategoryDto>())
    }

    @Test
    @DisplayName("Должен обновить существующую категорию")
    fun shouldUpdateExistingCategory() {
        whenever(categoryService.updateCategory(eq(id), any<RequestCategoryDto>())).thenReturn(categoryDto)

        mockMvc.perform(
            put("/api/v1/places/categories/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCategoryDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value(testCategory))
            .andExpect(jsonPath("$.slug").value(testCategory))

        verify(categoryService, times(1)).updateCategory(eq(id), any<RequestCategoryDto>())
    }

    @Test
    @DisplayName("Должен удалить категорию")
    fun shouldDeleteCategory() {
        mockMvc.perform(delete("/api/v1/places/categories/$id"))
            .andExpect(status().isOk)

        verify(categoryService, times(1)).deleteCategory(id)
    }
}