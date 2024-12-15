package kz.hustle.service;

import kz.hustle.dto.CityDTO;
import kz.hustle.entity.City;
import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
import kz.hustle.repository.CityRepository;
import kz.hustle.repository.CountryRepository;
import kz.hustle.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    public CityService(CityRepository cityRepository, RegionRepository regionRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<CityDTO> getCities(String country) {
        if (country == null) {
            return getAllCities();
        } else {
            if (isNumeric(country)) {
                return createDTOList(cityRepository
                        .findByCountryId(country));
            } else {
                return createDTOList(cityRepository
                        .findByCountryName(country));
            }
        }
    }

    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        return createDTO(cityRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("City with id " + id + " not found")));
    }

    @Transactional
    public CityDTO saveCity(City city) {
        return createDTO(cityRepository.save(city));
    }

    @Transactional
    public CityDTO createCity(CityDTO cityDTO) {
        // Fetch or create Country
        Country country = countryRepository.findByName(cityDTO.getCountryName())
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setName(cityDTO.getCountryName());
                    return countryRepository.save(newCountry);
                });

        // Fetch or create Region
        Region region = regionRepository.findByNameAndCountry(cityDTO.getRegionName(), country)
                .orElseGet(() -> {
                    Region newRegion = new Region();
                    newRegion.setName(cityDTO.getRegionName());
                    newRegion.setCountry(country);
                    return regionRepository.save(newRegion);
                });

        // Create and save City
        City city = new City();
        city.setName(cityDTO.getCityName());
        city.setRegion(region);

        return createDTO(cityRepository.save(city));
    }

    @Transactional
    public CityDTO updateCity(Long id, CityDTO updatedCity) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        existingCity.setName(updatedCity.getCityName());
        if (updatedCity.getRegionName() != null && !updatedCity.getRegionName().equals(existingCity.getRegion().getName())) {
            Region newRegion = regionRepository
                    .findByName(updatedCity.getRegionName())
                    .orElseThrow(() -> new IllegalArgumentException("Region " + updatedCity.getRegionName() + " not exists. Please create the region first."));
            existingCity.setRegion(newRegion);
        }
        return createDTO(cityRepository.save(existingCity));
    }

    @Transactional
    public boolean deleteCity(Long id) {
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Region getRegionForCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));
        return city.getRegion();
    }

    @Transactional(readOnly = true)
    public Country getCountryForCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));
        return city.getRegion().getCountry();
    }

    private List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    private List<CityDTO> createDTOList(List<City> cityList) {
        return cityList.stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    private CityDTO createDTO(City city) {
        return new CityDTO(
                city.getId(),
                city.getName(),
                city.getRegion().getName(),
                city.getRegion().getCountry().getName());
    }


}
