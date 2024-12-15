package kz.hustle.service;

import kz.hustle.entity.Country;
import kz.hustle.repository.CountryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Country> getAllCountries() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Country getCountryById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Country with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Country> getCountryByName(String name) {
        return repository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Optional<Country> getCountryByCode(String code) {
        return repository.findByCountryCode(code);
    }

    @Transactional
    public Country saveCountry(Country country) {
        try {
            return repository.save(country);
        } catch (DataIntegrityViolationException e) {
            throw new CountryAlreadyExistsException("Country " + country.getName() + " already exists.");
        }
    }

    @Transactional
    public Country updateCountry(Long id, Country updatedCountry) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country with id " + id + " not found"));
        String newName = updatedCountry.getName();
        String newCountryCode = updatedCountry.getCountryCode();
        if (newName != null) {
            existingCountry.setName(updatedCountry.getName());
        }
        if (newCountryCode != null) {
            existingCountry.setCountryCode(updatedCountry.getCountryCode());
        }
        try {
            return repository.saveAndFlush(existingCountry);
        } catch (DataIntegrityViolationException e) {
            throw new CountryAlreadyExistsException("Country " + newName + " already exists.");
        }
    }

    @Transactional
    public boolean deleteCountry(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

}
