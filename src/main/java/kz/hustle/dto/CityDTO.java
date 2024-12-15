package kz.hustle.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CityDTO {

    private Long cityId;

    @NotNull(message = "City name is required")
    @NotEmpty(message = "City name cannot be empty")
    private String cityName;

    private String regionName;

    private String countryName;

    public CityDTO() {
    }

    public CityDTO(String cityName, String regionName, String countryName) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
    }

    public CityDTO(Long cityId, String cityName, String regionName, String countryName) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
