package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryCustomRepository;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CountryService {
    private final CountryRepository repository;
    private final CountryCustomRepository customRepository;

    public List<CountryDTOWithUsers> findAllCountries() {
        List<Country> countries = repository.findAll();
        List<CountryDTOWithUsers> countryDTOs = new ArrayList<>();
        for (Country country : countries) {
            CountryDTOWithUsers countryDTO = Conversion.convertCountryToDTOWithUsers(country);
            countryDTOs.add(countryDTO);
        }
        return countryDTOs;
    }

    public void saveCountry(final Country country) {
        if (!repository.existsByName(country.getName())) {
            repository.save(country);
        } else {
            throw new ObjectAlreadyExistsException("Country with this name already exists");
        }
    }

    public CountryDTOWithUsers findByName(final String name) {
        CountryDTOWithUsers result = Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(name));
        if (result != null) {
            return result;
        } else {
            throw new ObjectNotFoundException("Requested non-existent county");
        }
    }

    public void deleteCountry(final String name) {
        if (repository.existsByName(name)) {
            repository.deleteByName(name);
        } else {
            throw new ObjectNotFoundException("Error deleting non-existent country");
        }
    }

    public void updateCountry(final Country country, final String countryOldName) {
        if (repository.existsByName(countryOldName)) {
            if (!repository.existsByName(country.getName())) {
                customRepository.updateCountry(country);
            } else {
                throw new ObjectAlreadyExistsException("Error updating country: new name already exists");
            }
        } else {
            throw new ObjectNotFoundException("Error updating non-existent country");
        }
    }
}
