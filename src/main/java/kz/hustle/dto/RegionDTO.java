package kz.hustle.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RegionDTO {

    private Long regionId;

    @NotNull(message = "Region name is required")
    @NotEmpty(message = "Region name cannot be empty")
    private String regionName;

    private String countryName;

    public RegionDTO() {
    }

    public RegionDTO(String regionName, String countryName) {
        this.regionName = regionName;
        this.countryName = countryName;
    }

    public RegionDTO(Long regionId, String regionName, String countryName) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.countryName = countryName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
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
