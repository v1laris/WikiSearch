package com.wks.wikisearch.serviceTests;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryCustomRepository;
import com.wks.wikisearch.repository.CountryRepository;
import com.wks.wikisearch.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTests {

    @Mock
    private CountryRepository repository;

    @Mock
    private CountryCustomRepository customRepository;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindAllCountries() {
        List<Country> countries = new ArrayList<>();
        Country country1 = new Country();
        country1.setId(1L);
        country1.setName("Country1");
        Country country2 = new Country();
        country2.setId(2L);
        country2.setName("Country2");
        countries.add(country1);
        countries.add(country2);

        when(repository.findAll()).thenReturn(countries);

        List<CountryDTOWithUsers> result = countryService.findAllCountries();

        assertEquals(countries.size(), result.size());}

    @Test
    void testSaveCountry_NewCountry() {
        Country country = new Country();
        country.setName("NewCountry");

        when(repository.existsByName(country.getName())).thenReturn(false);

        countryService.saveCountry(country);

        verify(repository, times(1)).save(country);
    }

    @Test
    void testSaveCountry_ExistingCountry() {
        Country country = new Country();
        country.setName("ExistingCountry");

        when(repository.existsByName(country.getName())).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> {
            countryService.saveCountry(country);
        });
    }

    @Test
    void testFindByName_ExistingCountry() {
        Country country = new Country();
        country.setName("ExistingCountry");

        when(repository.findCountryByName(country.getName())).thenReturn(country);

        CountryDTOWithUsers result = countryService.findByName(country.getName());

        assertNotNull(result);
        assertEquals(country.getName(), result.getName());
        // Add more assertions based on your specific requirements
    }

    @Test
    void testFindByName_NonExistingCountry() {
        String countryName = "NonExistingCountry";

        when(repository.findCountryByName(countryName)).thenReturn(null);

        assertThrows(ObjectNotFoundException.class, () -> {
            countryService.findByName(countryName);
        });
    }

    @Test
    void testDeleteCountry_ExistingCountry() {
        String countryName = "ExistingCountry";

        when(repository.existsByName(countryName)).thenReturn(true);

        countryService.deleteCountry(countryName);

        verify(repository, times(1)).deleteByName(countryName);
    }

    @Test
    void testDeleteCountry_NonExistingCountry() {
        String countryName = "NonExistingCountry";

        when(repository.existsByName(countryName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            countryService.deleteCountry(countryName);
        });
    }

    @Test
    void testUpdateCountry_ExistingCountry() {
        String oldCountryName = "ExistingCountry";
        Country country = new Country();
        country.setName("UpdatedCountry");

        when(repository.existsByName(oldCountryName)).thenReturn(true);
        when(repository.existsByName(country.getName())).thenReturn(false);

        countryService.updateCountry(country, oldCountryName);

        verify(customRepository, times(1)).updateCountry(country);
    }

    @Test
    void testUpdateCountry_ExistingNewCountryName() {
        String oldCountryName = "ExistingCountry";
        Country country = new Country();
        country.setName("ExistingNewCountryName");


        when(repository.existsByName(oldCountryName)).thenReturn(true);
        when(repository.existsByName(country.getName())).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> {
            countryService.updateCountry(country, oldCountryName);
        });
    }

    @Test
    void testUpdateCountry_NonExistingCountry() {
        String oldCountryName = "NonExistingCountry";
        Country country = new Country();
        country.setName("UpdatedCountry");

        when(repository.existsByName(oldCountryName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            countryService.updateCountry(country, oldCountryName);
        });
    }
}
