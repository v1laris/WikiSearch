package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
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
            CountryDTOWithUsers countryDTO = Convertation.convertCountryToDTOWithUsers(country);
            countryDTOs.add(countryDTO);
        }

        return  countryDTOs;
    }

    public void saveCountry(Country country) {
        repository.save(country);
    }
    public CountryDTOWithUsers findByName(String name) {
        return Convertation.convertCountryToDTOWithUsers(repository.findCountryByName(name));
    }

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
