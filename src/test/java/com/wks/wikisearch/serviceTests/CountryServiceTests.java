package com.wks.wikisearch.serviceTests;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryCustomRepository;
import com.wks.wikisearch.repository.CountryRepository;
import com.wks.wikisearch.service.CountryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceTests {

    @Mock
    private CountryRepository repository;

    @Mock
    private CountryCustomRepository customRepository;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;

    @Before
    public void setUp() {
        testCountry = new Country();
        testCountry.setName("Test Country");
    }

    @Test
    public void testFindAllCountries() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList(testCountry));

        // When
        List<CountryDTOWithUsers> result = countryService.findAllCountries();

        // Then
        assertEquals(1, result.size());
        assertEquals(testCountry.getName(), result.get(0).getName());
    }

    @Test
    public void testSaveCountry_Success() {
        // Given
        when(repository.existsByName(testCountry.getName())).thenReturn(false);

        // When
        countryService.saveCountry(testCountry);

        // Then
        verify(repository, times(1)).save(testCountry);
    }

    @Test(expected = ObjectAlreadyExistsException.class)
    public void testSaveCountry_AlreadyExists() {
        // Given
        when(repository.existsByName(testCountry.getName())).thenReturn(true);

        // When
        countryService.saveCountry(testCountry);
    }

    @Test
    public void testFindByName_ExistingCountry() {
        // Given
        when(repository.findCountryByName(testCountry.getName())).thenReturn(testCountry);

        // When
        CountryDTOWithUsers result = countryService.findByName(testCountry.getName());

        // Then
        assertEquals(testCountry.getName(), result.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindByName_NonExistingCountry() {
        // Given
        when(repository.findCountryByName(testCountry.getName())).thenReturn(null);

        // When
        countryService.findByName(testCountry.getName());
    }

    @Test
    public void testDeleteCountry_ExistingCountry() {
        // Given
        when(repository.existsByName(testCountry.getName())).thenReturn(true);

        // When
        countryService.deleteCountry(testCountry.getName());

        // Then
        verify(repository, times(1)).deleteByName(testCountry.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCountry_NonExistingCountry() {
        // Given
        when(repository.existsByName(testCountry.getName())).thenReturn(false);

        // When
        countryService.deleteCountry(testCountry.getName());
    }

    @Test
    public void testUpdateCountry_Success() {
        // Given
        String countryOldName = "Old Name";
        when(repository.existsByName(countryOldName)).thenReturn(true);
        when(repository.existsByName(testCountry.getName())).thenReturn(false);

        // When
        countryService.updateCountry(testCountry, countryOldName);

        // Then
        verify(customRepository, times(1)).updateCountry(testCountry);
    }

    @Test(expected = ObjectAlreadyExistsException.class)
    public void testUpdateCountry_NewNameAlreadyExists() {
        // Given
        String countryOldName = "Old Name";
        when(repository.existsByName(countryOldName)).thenReturn(true);
        when(repository.existsByName(testCountry.getName())).thenReturn(true);

        // When
        countryService.updateCountry(testCountry, countryOldName);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCountry_NonExistingCountry() {
        // Given
        String countryOldName = "Old Name";
        when(repository.existsByName(countryOldName)).thenReturn(false);

        // When
        countryService.updateCountry(testCountry, countryOldName);
    }
}

