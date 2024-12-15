package kz.hustle.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kz.hustle.entity.City;
import kz.hustle.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final CityRepository cityRepository;
    private static final String BASE_URL = "http://api.weatherapi.com/v1";
    @Value("${weatherapi.key}")
    private String API_KEY;

    public WeatherService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getCurrentWeatherByCityId(Long id) throws IOException, WeatherAPIForbiddenException, WeatherAPIUnauthorizedException {
        City city = cityRepository.findById(id).orElseThrow(() -> new RuntimeException("City not found"));
        Integer weatherApiCityId = getIdFromSearchAPI(
                city.getRegion().getCountry().getName(),
                city.getRegion().getName(),
                city.getName()
        );
        return getCurrentWeatherByWeatherApiCityId(weatherApiCityId);
    }

    private Integer getIdFromSearchAPI(String country, String region, String city) throws IOException {
        Integer result = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/search.json?key=" + API_KEY + "&q=" + city))
                    .GET()
                    .build();
            //Handle 403, 401
            HttpResponse<String> response = processRequest(request);
            JsonNode jsonArray = objectMapper.readTree(response.body());
            if (!jsonArray.isEmpty()) {
                //Taking the id of the first location in result set
                result = jsonArray.get(0).get("id").asInt();
                //If result set size is larger than one, try to look for an exact coincidence
                if (jsonArray.size() > 1) {
                    for (JsonNode jsonNode : jsonArray) {
                        if (jsonNode.get("country").asText().equals(country) && jsonNode.get("region").asText().equals(region)) {
                            result = jsonNode.get("id").asInt();
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JsonNode getCurrentWeatherForCitySearch(String city, String region, String country) throws IOException {
        ArrayNode result = objectMapper.createArrayNode();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search.json?key=" + API_KEY + "&q=" + city))
                .GET()
                .build();
        HttpResponse<String> response = processRequest(request);
        JsonNode jsonArray = objectMapper.readTree(response.body());

        for (JsonNode jsonNode : jsonArray) {
            Integer id = jsonNode.get("id").asInt();
            if (region == null && country == null) {
                result.add(getCurrentWeatherByWeatherApiCityId(id));
            } else {
                if (region != null) {
                    if (country != null) {
                        if ((jsonNode.get("country").asText().equals(country)) && (jsonNode.get("region").asText().equals(region))) {
                            result.add(getCurrentWeatherByWeatherApiCityId(id));
                        }
                    } else if (jsonNode.get("region").asText().equals(region)) {
                        result.add(getCurrentWeatherByWeatherApiCityId(id));
                    }
                } else if ((jsonNode.get("country").asText().equals(country))) {
                    result.add(getCurrentWeatherByWeatherApiCityId(id));
                }
            }
        }
        return result;
    }

    public JsonNode getWeatherForecastByCityId(Long id, Integer days) throws IOException {
        City city = cityRepository.findById(id).orElseThrow(() -> new RuntimeException("City not found"));
        Integer weatherApiCityId = getIdFromSearchAPI(
                city.getRegion().getCountry().getName(),
                city.getRegion().getName(),
                city.getName()
        );
        return getWeatherForecastByWeatherApiCityId(weatherApiCityId, days);
    }

    private JsonNode getCurrentWeatherByWeatherApiCityId(Integer id) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/current.json?key=" + API_KEY + "&q=id:" + id))
                .GET()
                .build();
        HttpResponse<String> response = processRequest(request);
        return objectMapper.readTree(response.body());
    }

    private JsonNode getWeatherForecastByWeatherApiCityId(Integer id, Integer days) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL
                        + "/forecast.json?key=" + API_KEY
                        + "&q=id:" + id
                        + "&days=" + days
                        + "&hour=25")) // hour = 25 to omit hours from the forecast
                .GET()
                .build();
        HttpResponse<String> response = processRequest(request);
        return objectMapper.readTree(response.body());
    }

    private HttpResponse<String> processRequest(HttpRequest request) throws IOException {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 403) {
                throw new WeatherAPIForbiddenException("Weather API key has been disabled. Please check that your API key is correct.");
            } else if (response.statusCode() == 401) {
                throw new WeatherAPIUnauthorizedException("Weather API key is invalid or not exists. Please check you API key.");
            } else if (response.statusCode() == 408) {
                throw new WeatherAPIRequestTimeoutException("The request timed out. Ensure the Weather API is reachable and try again.");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Operation was interrupted", e);
        }
        return response;
    }
}
