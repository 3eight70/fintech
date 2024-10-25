import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import ru.fintech.kotlin.HomeworkApplication
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.location.dto.LocationDto
import ru.fintech.kotlin.location.entity.Location
import ru.fintech.kotlin.location.impl.LocationServiceImpl

/**
 * Хотел сделать тесты только для категорий, т.к по сути все идентично, но нехватило на 0.7 ;)
 */
@SpringBootTest(classes = [HomeworkApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LocationServiceTest {
    private lateinit var locationService: LocationServiceImpl
    private lateinit var locationRepository: CustomGenericRepository<Location>

    @BeforeEach
    fun setup() {
        locationRepository = mock()
        locationService = LocationServiceImpl(locationRepository)
    }

    @Test
    @DisplayName("Должен возвращать список локаций")
    fun shouldReturnLocations() {
        val locations = listOf(
            Location(id = 1, name = "Location1", slug = "location1"),
            Location(id = 2, name = "Location2", slug = "location2")
        )
        whenever(locationRepository.findAll()).thenReturn(locations)

        val result = locationService.getLocations()

        verify(locationRepository, times(1)).findAll()
        assert(result.size == 2)
        assert(result[0].name == "Location1")
        assert(result[1].name == "Location2")
    }

    @Test
    @DisplayName("Должен возвращать локацию по id")
    fun shouldReturnLocationById() {
        val location = Location(id = 1, name = "Location1", slug = "location1")
        whenever(locationRepository.findById(1)).thenReturn(location)

        val result = locationService.getLocation(1)

        verify(locationRepository, times(1)).findById(1)
        assert(result != null)
        assert(result?.name == "Location1")
    }

    @Test
    @DisplayName("Должен возвращать null, если локация не найдена")
    fun shouldReturnNullWhenLocationNotFound() {
        whenever(locationRepository.findById(1)).thenReturn(null)

        val result = locationService.getLocation(1)

        verify(locationRepository, times(1)).findById(1)
        assert(result == null)
    }

    @Test
    @DisplayName("Должен создавать новую локацию")
    fun shouldCreateLocation() {
        val locationDto = LocationDto(id = 123, name = "new", slug = "new")
        val savedLocation = Location(id = 1, name = "new", slug = "new")
        whenever(locationRepository.save(any())).thenReturn(savedLocation)

        val result = locationService.createLocation(locationDto)

        verify(locationRepository, times(1)).save(any())
        assert(result.name == "new")
        assert(result.slug == "new")
    }

    @Test
    @DisplayName("Должен обновлять существующую локацию")
    fun shouldUpdateLocation() {
        val existingLocation = Location(id = 1, name = "old", slug = "old")
        val updateDto = LocationDto(id = 123, name = "new", slug = "new")
        whenever(locationRepository.findById(1)).thenReturn(existingLocation)
        whenever(locationRepository.save(any())).thenReturn(existingLocation)

        val result = locationService.updateLocation(1, updateDto)

        verify(locationRepository, times(1)).findById(1)
        verify(locationRepository, times(1)).save(any())
        assert(result.name == "new")
        assert(result.slug == "new")
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если локация для обновления не найдена")
    fun shouldThrowExceptionWhenUpdatingNonExistentLocation() {
        val updateDto = LocationDto(id = 123, name = "new", slug = "new")
        whenever(locationRepository.findById(1)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            locationService.updateLocation(1, updateDto)
        }
    }

    @Test
    @DisplayName("Должен удалять локацию по id")
    fun shouldDeleteLocation() {
        locationService.deleteLocation(1)

        verify(locationRepository, times(1)).delete(1)
    }
}
