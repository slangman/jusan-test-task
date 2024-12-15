package kz.hustle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.hustle.dto.CityDTO;
import kz.hustle.entity.City;
import kz.hustle.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
@Tag(name = "City API", description = "Operations related to city management")
public class CityController {

    private CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @Operation(summary = "Get all cities", description = "Retrieves a list of all cities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public List<CityDTO> getAllCities(
            @RequestParam(required = false) String country
    ) {
        return cityService.getCities(country);
    }

    @Operation(summary = "Get city by ID", description = "Fetch a specific city by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "City found"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable Long id) {
        try {
            CityDTO city = cityService.getCityById(id);
            return ResponseEntity.ok(city);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Add a new city.", description = "Creates a new city with the provided City object. If city is " +
            "related to a region or country that are not exist in database, then the region or country must be created first.")
    @PostMapping("/create")
    public CityDTO createCity(@RequestBody City city) {
        return cityService.saveCity(city);
    }

    /**
     * Creates in the database the record for a given city
     *
     * @param cityDTO the JSON object with the following structure:
     *                {
     *                "cityName": "New City",
     *                "regionName": "New Region",
     *                "countryName": "New Country"
     *                }
     * @return
     */
    @Operation(summary = "Add a new city. More simple method.", description = "Creates a new city with the provided details " +
            "(City name, Region name, Country name). If region or/and country not exist in database, they are also being created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "City created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create-simple")
    public ResponseEntity<CityDTO> createCitySimple(@Valid @RequestBody CityDTO cityDTO) {
        CityDTO newCity = cityService.createCity(cityDTO);
        return ResponseEntity.ok(newCity);
    }

    @Operation(summary = "Update city by ID", description = "Update a specific city by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable Long id, @RequestBody CityDTO updatedCity) {
        try {
            CityDTO city = cityService.updateCity(id, updatedCity);
            return ResponseEntity.ok(city);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete city by ID", description = "Delete a specific city by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {
        if (cityService.deleteCity(id)) {
            return ResponseEntity.ok("City with id " + id + " deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
