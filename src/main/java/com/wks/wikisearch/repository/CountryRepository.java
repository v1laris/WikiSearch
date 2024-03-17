package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    void deleteByName(String name);
    Country findCountryByName(String name);

}