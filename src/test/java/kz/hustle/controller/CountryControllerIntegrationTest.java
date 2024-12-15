package kz.hustle.controller;

import kz.hustle.entity.Country;
import kz.hustle.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CountryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    public void setUp() {
        countryRepository.deleteAll();
    }

    @Test
    public void testGetAllCountries() {
        Country country1 = new Country("KZ", "Kazakhstan");
        Country country2 = new Country("US", "USA");
        countryRepository.save(country1);
        countryRepository.save(country2);
        ResponseEntity<Country[]> response = restTemplate.getForEntity("/country/all", Country[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void testGetCountryById() {
        Country country = countryRepository.save(new Country("KZ", "Kazakhstan"));

        ResponseEntity<Country> response = restTemplate.getForEntity("/country/{id}", Country.class, country.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kazakhstan", response.getBody().getName());
    }

    @Test
    public void testCreateCountry() {
        Country newCountry = new Country("KZ", "Kazakhstan");

        ResponseEntity<Country> response = restTemplate.postForEntity("/country", newCountry, Country.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kazakhstan", response.getBody().getName());
    }

    @Test
    public void testUpdateCountry() {
        Country existingCountry = restTemplate
                .postForEntity("/country", new Country("KZ", "Kazakhstan"), Country.class)
                .getBody();
        Country newCountry = new Country("QR", "Qazaq Republic");
        assert existingCountry != null;
        restTemplate.put("/country/{id}", newCountry, existingCountry.getId());
        ResponseEntity<Country> response = restTemplate.getForEntity("/country/{id}", Country.class, existingCountry.getId());
        assertNotNull(response.getBody());
        assertEquals("QR", response.getBody().getCountryCode());
        assertEquals("Qazaq Republic", response.getBody().getName());
    }

    @Test
    public void testDeleteCountry() {
        Country country = countryRepository.save(new Country("KZ", "Kazakhstan"));

        restTemplate.delete("/country/{id}", country.getId());

        Optional<Country> deletedCountry = countryRepository.findById(country.getId());
        assertFalse(deletedCountry.isPresent());
    }

}
