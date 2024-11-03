package ru.tinkoff.fintech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tinkoff.fintech.place.dto.PlaceRequestDto;
import ru.tinkoff.fintech.user.dto.LoginCredentials;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.entity.User;
import ru.tinkoff.fintech.user.enums.Role;
import ru.tinkoff.fintech.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ru.tinkoff.fintech.utils.TokenUtils.extractTokenFromResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class PlaceControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    private UUID placeId;
    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();

        var user = new User(
          UUID.randomUUID(),
          "admin",
          "$2a$10$zjrFrBjRYaJaDYNKzW0pge638G4.Q4GEDWmAFV.Kdn8aq9PcwxTFi",
          Role.ADMIN
        );
        userRepository.save(user);
        var loginCredentials = new LoginCredentials(
                "admin",
                "admin",
                false
        );

        String loginResponse = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginCredentials)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = extractTokenFromResponse(loginResponse);

        PlaceRequestDto placeRequestDto = new PlaceRequestDto("Тестовое место", "test-place", "Описание тестового места");
        String response = mockMvc.perform(post("/api/places")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Тестовое место\", \"slug\":\"test-place\", \"description\":\"Описание тестового места\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        placeId = extractPlaceIdFromResponse(response);
    }

    @Test
    @DisplayName("Место должно быть создано и сохранено")
    public void testCreatePlace() throws Exception {
        mockMvc.perform(get("/api/places/" + placeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Тестовое место"));
    }

    @Test
    @DisplayName("Место должно быть обновлено")
    public void testUpdatePlace() throws Exception {
        mockMvc.perform(put("/api/places/" + placeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Обновленное место\", \"slug\":\"updated-place\", \"description\":\"Обновленное описание\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(get("/api/places/" + placeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Обновленное место"));
    }

    @Test
    @DisplayName("Место должно быть удалено")
    public void testDeletePlace() throws Exception {
        mockMvc.perform(delete("/api/places/" + placeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(get("/api/places/" + placeId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private UUID extractPlaceIdFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return UUID.fromString(jsonNode.get("id").asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при извлечении ID места из ответа", e);
        }
    }
}