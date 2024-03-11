package com.wks.wikisearch.service;

import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl {
    private final CountryRepository repository;
    public List<Country> findAllCountries() {
        return repository.findAll();
    }

    public void saveCountry(Country country) {
        repository.save(country);
    }
    public Country findByName(String name) {
        return repository.findCountryByName(name);
    }

    @Transactional
    public void deleteCountry(String name) {
        repository.deleteByName(name);
    }

    public String updateCountry(Country country) {
        if(repository.existsById(country.getId())) {
            String sql = "UPDATE country SET name = ? WHERE id = ?";
            jdbcTemplate.update(sql, country.getName(), country.getId());
            return "Country updated.";
        }
        return "Country is not updated.";
    }
    private final JdbcTemplate jdbcTemplate;
}
