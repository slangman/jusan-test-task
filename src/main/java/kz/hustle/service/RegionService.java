package kz.hustle.service;

import kz.hustle.dto.RegionDTO;
import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
import kz.hustle.repository.CountryRepository;
import kz.hustle.repository.RegionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    public RegionService(RegionRepository regionRepository, CountryRepository countryRepository) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<RegionDTO> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::createDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegionDTO> getRegionsByCountry(String countryParam) {
        Country country;
        if (isNumeric(countryParam)) {
            country = countryRepository
                    .findById(Long.parseLong(countryParam))
                    .orElseThrow(() -> new IllegalArgumentException("Country with id " + countryParam + " not found."));
        } else {
            country = countryRepository
                    .findByName(countryParam)
                    .orElseThrow(() -> new IllegalArgumentException("Country " + countryParam + " not found."));
        }
        return regionRepository.findByCountry(country)
                .stream()
                .map(this::createDTO
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public RegionDTO getRegionById(Long id) {
        return createDTO(regionRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Region with id " + id + " not found")));
    }

    @Transactional
    public RegionDTO saveRegion(Region region) {
        return createDTO(regionRepository.save(region));
    }

    @Transactional
    public RegionDTO createRegion(RegionDTO regionDTO) {
        Country country = countryRepository.findByName(regionDTO.getCountryName())
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setName(regionDTO.getCountryName());
                    try {
                        return countryRepository.save(newCountry);
                    } catch (DataIntegrityViolationException e) {
                        throw new CountryAlreadyExistsException("Country " + newCountry.getName() + " already exists.");
                    }
                });
        Region region = new Region();
        region.setName(regionDTO.getRegionName());
        region.setCountry(country);

        try {
            return createDTO(regionRepository.save(region));
        } catch (DataIntegrityViolationException e) {
            throw new RegionAlreadyExistsException("Region " + region.getName() + " already exists in " + country.getName());
        }
    }

    @Transactional
    public RegionDTO updateRegion(Long id, RegionDTO regionDTO) {
        String countryName = regionDTO.getCountryName();
        Region region = regionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Region with " + id + " not found."));
        if (countryName != null) {
            Country country = countryRepository.findByName(countryName)
                    .orElseThrow(() -> new IllegalArgumentException("Country " + regionDTO.getCountryName() + " not found. Please create the country first of check country name."));
            region.setCountry(country);
        }
        region.setName(regionDTO.getRegionName());
        return createDTO(regionRepository.save(region));
    }

    @Transactional
    public boolean deleteRegion(Long id) {
        if (regionRepository.existsById(id)) {
            regionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private RegionDTO createDTO(Region region) {
        return new RegionDTO(region.getId(), region.getName(), region.getCountry().getName());
    }
}
