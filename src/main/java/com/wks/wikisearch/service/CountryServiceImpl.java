package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CountryServiceImpl {
    private final CountryRepository repository;
    public List<CountryDTOWithUsers> findAllCountries() {
        List<Country> countries = repository.findAll();
        List<CountryDTOWithUsers> countryDTOs = new ArrayList<>();
        for(Country country : countries){
            CountryDTOWithUsers countryDTO = Conversion.convertCountryToDTOWithUsers(country);
            countryDTOs.add(countryDTO);
        }

        return  countryDTOs;
    }

    public Country saveCountry(Country country) {
        try {
            return repository.save(country);
        }
        catch (DataIntegrityViolationException ex){
            return null;
        }
    }
    public CountryDTOWithUsers findByName(String name) {
        return Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(name));
    }

    public void deleteCountry(String name) {
        repository.deleteByName(name);
    }

    public void updateCountry(Country country) {
        Country temp = repository.findCountryByName(country.getName());
        temp.setName(country.getName());
        repository.save(temp);
    }
}
