package kz.hustle.repository;

import kz.hustle.entity.City;
import kz.hustle.entity.Country;
import kz.hustle.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE c.region.country.name = :countryName")
    List<City> findByCountryName(@Param("countryName") String countryName);

    @Query("SELECT c FROM City c WHERE c.region.country.id = :countryId")
    List<City> findByCountryId(@Param("countryId") String countryId);
}
