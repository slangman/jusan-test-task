package kz.hustle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.hustle.dto.RegionDTO;
import kz.hustle.entity.Region;
import kz.hustle.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/region")
@Tag(name = "Region API", description = "Operations related to region management")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @Operation(summary = "Get regions list", description = "Retrieves a list of all regions. " +
            "If country parameter passed, returns only the regions of the specified country.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "404", description = "Specified country not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllRegions(
            @Parameter(description = "Name or id of the country",
                    examples = {
                            @ExampleObject(
                                    name = "Country Name",
                                    value = "India"
                            ),
                            @ExampleObject(
                                    name = "Country Id",
                                    value = "1"
                            )
                    },
                    required = false)
            @RequestParam(required = false) String country
    ) {
        try {
            List<RegionDTO> result;
            if (country != null) {
                result = regionService.getRegionsByCountry(country);
            } else {
                result = regionService.getAllRegions();
            }
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get region by ID", description = "Fetch a specific region by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegionById(@PathVariable Long id) {
        try {
            RegionDTO region = regionService.getRegionById(id);
            return ResponseEntity.ok(region);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Add a new region.", description = "Creates a new region with the provided Region object. If region is " +
            "related to a country that is not exist in database, then the country must be created first.")
    @PostMapping()
    public RegionDTO createRegion(@RequestBody Region region) {
        return regionService.saveRegion(region);
    }

    @Operation(summary = "Add a new region. More simple method.", description = "Creates a new region with the provided details " +
            "(Region name, Country name). If country not exist in database, it is also being created.")
    @PostMapping("/create-simple")
    public ResponseEntity<RegionDTO> createRegionSimple(@RequestBody RegionDTO regionDTO) {
        RegionDTO newRegion = regionService.createRegion(regionDTO);
        return ResponseEntity.ok(newRegion);
    }

    @Operation(summary = "Update region by ID", description = "Update a specific region by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @RequestBody RegionDTO updatedRegion) {
        try {
            RegionDTO region = regionService.updateRegion(id, updatedRegion);
            return ResponseEntity.ok(region);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete region by ID", description = "Delete a specific region by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRegion(@PathVariable Long id) {
        if (regionService.deleteRegion(id)) {
            return ResponseEntity.ok("Region " + id + " deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
