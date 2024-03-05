package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.AppUserRepository;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
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

    public String saveCountry(Country country) {
        repository.save(country);
        return "Country saved.";
    }
    public Country findByName(String name) {
        return repository.findCountryByName(name);
    }
    public String updateCountry(Country country) {
        repository.save(country);
        return "Country updated.";
    }

    @Transactional
    public void deleteCountry(String name) {
        repository.deleteByName(name);
    }

}
