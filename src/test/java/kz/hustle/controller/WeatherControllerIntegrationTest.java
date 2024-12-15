package kz.hustle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.hustle.dto.CityDTO;
import kz.hustle.entity.City;
import kz.hustle.service.CityService;
import kz.hustle.service.WeatherService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class WeatherControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CityService cityService;

    private Long cityId;

    @BeforeAll
    void setupTestEnv() {
        cityId = cityService.createCity(new CityDTO("Almaty", "Almaty City", "Kazakhstan")).getCityId();
    }

    @Test
    public void testGetCurrentWeatherByCityId() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/weather/current/{cityId}", String.class, cityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JsonNode result = new ObjectMapper().readTree(response.getBody());
        assertTrue(result.has("location"));
        assertTrue(result.has("current"));
        assertEquals("Almaty", result.get("location").get("name").asText());
    }

    @Test
    public void testGetCurrentWeatherForCitySearch() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/weather/current?city=Hyderabad", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JsonNode resultArray = new ObjectMapper().readTree(response.getBody());
        assertEquals(2, resultArray.size());
        assertEquals("Hyderabad", resultArray.get(0).get("location").get("name").asText());
        assertEquals(resultArray.get(0).get("location").get("name").asText(), resultArray.get(1).get("location").get("name").asText());
        assertNotEquals(resultArray.get(0).get("location").get("country").asText(), resultArray.get(1).get("location").get("country").asText());
    }

    @Test
    public void getWeatherForecastByCityId() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/weather/forecast?cid={cityId}&days=3", String.class, cityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JsonNode result = new ObjectMapper().readTree(response.getBody());
        assertTrue(result.has("forecast"));
        assertEquals(3, result.get("forecast").get("forecastday").size());
    }
}
