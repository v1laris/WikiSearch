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

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CountryService {
    private final CountryRepository repository;
    private final CountryCustomRepository customRepository;

    public List<CountryDTOWithUsers> findAllCountries() {
        return repository.findAll().stream()
                .map(Conversion::convertCountryToDTOWithUsers)
                .toList();
    }

    public void saveCountry(final Country country) {
        if (!repository.existsByName(country.getName())) {
            repository.save(country);
        } else {
            throw new ObjectAlreadyExistsException("Country with this name already exists");
        }
    }

    public CountryDTOWithUsers findByName(final String name) {
        CountryDTOWithUsers result =
                Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(name));
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
        if (!repository.existsByName(countryOldName)) {
            throw new ObjectNotFoundException("Error updating non-existent country");
        }
        if (repository.existsByName(country.getName())) {
            throw new ObjectAlreadyExistsException("Error updating country: new name already exists");
        }
        customRepository.updateCountry(country);

    }
}
