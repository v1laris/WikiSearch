package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.exception.ResourceAlreadyExistsException;
import com.wks.wikisearch.exception.ResourceNotFoundException;
import com.wks.wikisearch.model.CachePrimaryKeys;
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
    private final CacheService cacheService;

    public List<CountryDTOWithUsers> findAllCountries() {
        List<CountryDTOWithUsers> countryDTOWithUsers = cacheService.findAllCountries();
        if (countryDTOWithUsers != null) {
            return countryDTOWithUsers;
        } else {
            List<Country> countries = repository.findAll();
            List<CountryDTOWithUsers> countryDTOs = new ArrayList<>();
            for (Country country : countries) {
                CountryDTOWithUsers countryDTO = Conversion.convertCountryToDTOWithUsers(country);
                countryDTOs.add(countryDTO);
            }
            CacheManager.put(CachePrimaryKeys.COUNTRY_PRIMARY_KEY, countryDTOs);
            for (CountryDTOWithUsers country : countryDTOs) {
                cacheService.addCountry(country);
            }
            return countryDTOs;
        }
    }

    public void saveCountry(final Country country) {
        if (!repository.existsByName(country.getName())) {
            repository.save(country);
            cacheService.addCountryToList(country);
        } else {
            throw new ResourceAlreadyExistsException("Country with this name already exists");
        }
    }

    public CountryDTOWithUsers findByName(final String name) {
        CountryDTOWithUsers result = cacheService.getCountry(name);
        if (result != null) {
            return result;
        } else {
            result = Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(name));
            if (result != null) {
                cacheService.addCountry(result);
                return result;
            } else {
                throw new ResourceNotFoundException("Requested non-existent county");
            }
        }
    }

    public void deleteCountry(final String name) {
        if (repository.existsByName(name)) {
            cacheService.removeCountry(name);
            repository.deleteByName(name);
        } else {
            throw new ResourceNotFoundException("Error deleting non-existent country");
        }
    }

    public void updateCountry(final Country country, final String countryOldName) {
        if (repository.existsByName(countryOldName)) {
            if (!repository.existsByName(country.getName())) {
                customRepository.updateCountry(country);
                cacheService.updateCountry(repository.findCountryByName(country.getName()), countryOldName);
            } else {
                throw new ResourceAlreadyExistsException("Error updating country: new name already exists");
            }
        } else {
            throw new ResourceNotFoundException("Error updating non-existent country");
        }
    }
}
