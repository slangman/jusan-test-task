package kz.hustle.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.hustle.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Exceptions are handled globally by GlobalExceptionHandler
@RestController
@RequestMapping("/weather")
@Tag(name = "Weather API", description = "Operations related to retrieving weather data from https://www.weatherapi.com/")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get weather by city id", description = "Retrieves from Weather API current weather for the city " +
            "that is stored in our application database.")
    @GetMapping("/current/{cityId}")
    public ResponseEntity<?> getCurrentWeatherByCityId(
            @Parameter(description = "Id of the city from internal database")
            @PathVariable Long cityId
    ) throws Exception {
        JsonNode result = weatherService.getCurrentWeatherByCityId(cityId);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the current weather for cities based on the provided parameters.
     * - If only the city name is provided (without region or country), the weather for all cities with the same name is returned.
     * - If a region and/or country is specified, these parameters are used to refine the city search.
     * @param city
     * @param region
     * @param country
     * @return
     * @throws Exception
     */
    @Operation(summary = "Get current weather for cities with provided params", description = "Redirects our request directly to" +
            "Weather API. If just a city name provided, retrieves current weather for all the cities with the same name (e.g. if " +
            "pass \"Hyderabad\", it returns results for two cities with the same name located in different countries (India, Pakistan). For retrieving " +
            "the result for specified city, optional parameters for region and country may be provided.")
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeatherForCitySearch(
            @Parameter(description = "City name", required = true)
            @RequestParam String city,

            @Parameter(description = "Region name")
            @RequestParam(required = false) String region,

            @Parameter(description = "Country name")
            @RequestParam(required = false) String country
    ) throws Exception {
        JsonNode result = weatherService.getCurrentWeatherForCitySearch(city, region, country);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Get weather forecast.", description = "Returns weather forecast for the city with specified Id " +
            "for the specified number of days.")
    @GetMapping("/forecast")
    public ResponseEntity<?> getWeatherForecastByCityId(
            @Parameter(description = "Id of the city from internal database", required = true)
            @RequestParam Long cid,

            @Parameter(description = "Number of forecast days", required = true)
            @RequestParam Integer days) throws Exception {
        JsonNode result = weatherService.getWeatherForecastByCityId(cid, days);
        return ResponseEntity.ok(result);
    }
}
