package kz.hustle.controller;

import kz.hustle.dto.CityDTO;
import kz.hustle.entity.City;
import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
import kz.hustle.repository.CityRepository;
import kz.hustle.repository.CountryRepository;
import kz.hustle.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CityControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    public void setUp() {
        cityRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    public void getAllCitiesTest() {
        Country country1 = new Country("KZ", "Kazakhstan");
        Country country2 = new Country("US", "USA");
        Region region1 = new Region(country1, "South Kazakhstan");
        Region region2 = new Region(country2, "Texas");
        City city1 = new City("Shymkent", region1);
        City city2 = new City("Dallas", region2);
        countryRepository.saveAll(List.of(country1, country2));
        regionRepository.saveAll(List.of(region1, region2));
        cityRepository.saveAll(List.of(city1, city2));
        ResponseEntity<City[]> response = restTemplate.getForEntity("/city/all", City[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void createCityTest() {
        Country country = countryRepository.save(new Country("KZ", "Kazakhstan"));
        Region region = regionRepository.save(new Region(country, "South Kazakhstan"));
        Long cityId = cityRepository.save(new City("Shymkent", region)).getId();
        ResponseEntity<CityDTO> response = restTemplate.getForEntity("/city/{id}", CityDTO.class, cityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Shymkent", response.getBody().getCityName());
        assertEquals("South Kazakhstan", response.getBody().getRegionName());
        assertEquals("Kazakhstan", response.getBody().getCountryName());
    }

    @Test
    public void createCitySimpleTest() {
        CityDTO cityDTO = new CityDTO("Shymkent", "South Kazakhstan", "Kazakhstan");
        ResponseEntity<CityDTO> response = restTemplate.postForEntity("/city/create-simple", cityDTO, CityDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Shymkent", response.getBody().getCityName());
        assertEquals("Kazakhstan", response.getBody().getCountryName());
        assertEquals("South Kazakhstan", response.getBody().getRegionName());
    }

    @Test
    public void updateCityTest() {
        Country country = countryRepository.save(new Country("KZ", "Kazakhstan"));
        Region region = regionRepository.save(new Region(country, "South Kazakhstan"));
        Long cityId = cityRepository.save(new City("Shymkent", region)).getId();
        CityDTO newCity = new CityDTO();
        newCity.setCityName("Dallas");
        restTemplate.put("/city/{id}", newCity, cityId);
        ResponseEntity<CityDTO> response = restTemplate.getForEntity("/city/{id}", CityDTO.class, cityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dallas", response.getBody().getCityName());
        assertEquals("Kazakhstan", response.getBody().getCountryName());
        assertEquals("South Kazakhstan", response.getBody().getRegionName());
    }

    @Test
    public void deleteCityTest() {
        CityDTO city = restTemplate
                .postForEntity("/city/create-simple", new CityDTO("Shymkent", "South Kazakhstan", "Kazakhstan"), CityDTO.class).getBody();

        restTemplate.delete("/city/{id}", city.getCityId());

        Optional<City> deletedCity = cityRepository.findById(city.getCityId());
        assertFalse(deletedCity.isPresent());
    }
}
