package kz.hustle.repository;

import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);

    Optional<Region> findByNameAndCountry(String name, Country country);

    List<Region> findByCountry(Country country);
}
