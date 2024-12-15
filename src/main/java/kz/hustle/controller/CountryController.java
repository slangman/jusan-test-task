package kz.hustle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.hustle.entity.Country;
import kz.hustle.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/country")
@Tag(name = "Country API", description = "Operations related to country management")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @Operation(summary = "Get all countries", description = "Retrieves a list of all countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @Operation(summary = "Get country by ID", description = "Fetch a specific country by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country found"),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCountryByCountryId(@PathVariable Long id) {
        try {
            Country result = countryService.getCountryById(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Add a new country", description = "Creates a new country with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Country details",
                            required = true,
                            content = @Content(
                                mediaType = "application/json",
                                examples = {
                                        @ExampleObject(
                                                name = "Country name with country code",
                                                value = "{\"name\": \"Kazakhstan\", \"countryCode\": \"KZ\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Just country name",
                                                value = "{\"name\": \"Kazakhstan\"}",
                                                description = "Country will be added with null country code"
                                        )
                                }
            )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Country created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping()
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        Country newCountry = countryService.saveCountry(country);
        return ResponseEntity.ok(newCountry);
    }


    @Operation(summary = "Update country by ID", description = "Update a specific country by its ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New country details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Update country name and country code",
                                            value = "{\"name\": \"Qazaq Republic\", \"countryCode\": \"Qr\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Update only country name",
                                            value = "{\"name\": \"Qazaq Republic\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Long id, @RequestBody Country updatedCountry) {
        try {
            Country country = countryService.updateCountry(id, updatedCountry);
            return ResponseEntity.ok(country);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete country by ID", description = "Delete a specific country by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable Long id) {
        if (countryService.deleteCountry(id)) {
            return ResponseEntity.ok(String.format("Country with id %d deleted successfully.", id));
        } else {
            return ResponseEntity.badRequest().body("Country with id " + id + " not found.");
        }
    }

}
