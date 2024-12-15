package kz.hustle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kz.hustle.dto.RegionDTO;
import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
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
public class RegionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    public void setUp() {
        countryRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    public void testGetAllRegions() {
        Country country1 = new Country("KZ", "Kazakhstan");
        Country country2 = new Country("US", "USA");
        Region region1 = new Region(country1, "South Kazakhstan");
        Region region2 = new Region(country2, "Texas");
        countryRepository.saveAll(List.of(country1, country2));
        regionRepository.saveAll(List.of(region1, region2));
        ResponseEntity<Region[]> response = restTemplate.getForEntity("/region/all", Region[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void testGetAllRegionsByCountry() {
        Country country1 = countryRepository.save(new Country("KZ", "Kazakhstan"));
        regionRepository.save(new Region(country1, "Jetisu"));
        regionRepository.save(new Region(country1, "Almaty"));
        Country country2 = countryRepository.save(new Country("KG", "Kyrgyzstan"));
        regionRepository.save(new Region(country2, "Chui"));

        ResponseEntity<Region[]> response1 = restTemplate.getForEntity("/region/all?country={country}", Region[].class, country1.getId());
        ResponseEntity<Region[]> response2 = restTemplate.getForEntity("/region/all?country={country}", Region[].class, country2.getId());
        ResponseEntity<Region[]> response3 = restTemplate.getForEntity("/region/all?country={country}", Region[].class, country2.getName());

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertNotNull(response1.getBody());
        assertEquals(2, response1.getBody().length);

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotNull(response2.getBody());
        assertEquals(1, response2.getBody().length);

        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertNotNull(response3.getBody());
        assertEquals(1, response3.getBody().length);
    }

    @Test
    public void testGetRegionById() {
        Country country = countryRepository.save(new Country("KZ", "Kazakhstan"));
        Region region = regionRepository.save(new Region(country, "South Kazakhstan"));
        ResponseEntity<RegionDTO> response = restTemplate.getForEntity("/region/{id}", RegionDTO.class, region.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("South Kazakhstan", response.getBody().getRegionName());
    }

    @Test
    public void testCreateRegionSimple() throws JsonProcessingException {
        RegionDTO regionDTO = new RegionDTO("South Kazakhstan", "Kazakhstan");
        ResponseEntity<RegionDTO> response = restTemplate.postForEntity("/region/create-simple", regionDTO, RegionDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kazakhstan", response.getBody().getCountryName());
        assertEquals("South Kazakhstan", response.getBody().getRegionName());
    }

    @Test
    public void testUpdateRegion() {
        RegionDTO existingRegion = restTemplate
                .postForEntity("/region/create-simple", new RegionDTO("Jetisu", "Kazakhstan"), RegionDTO.class).getBody();
        RegionDTO newRegion = new RegionDTO("Taldykorgan", "Kazakhstan");
        restTemplate.put("/region/{id}", newRegion, existingRegion.getRegionId());
        ResponseEntity<RegionDTO> response = restTemplate.getForEntity("/region/{id}", RegionDTO.class, existingRegion.getRegionId());
        assertNotNull(response.getBody());
        assertEquals("Taldykorgan", response.getBody().getRegionName());
    }

    @Test
    public void testDeleteRegion() {
        RegionDTO region = restTemplate
                .postForEntity("/region/create-simple", new RegionDTO("Jetisu", "Kazakhstan"), RegionDTO.class).getBody();

        restTemplate.delete("/region/{id}", region.getRegionId());

        Optional<Region> deletedRegion = regionRepository.findById(region.getRegionId());
        assertFalse(deletedRegion.isPresent());
    }

}
