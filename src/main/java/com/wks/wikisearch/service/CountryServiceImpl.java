package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.dto.UserDTO;
import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.CountryCustomRepository;
import com.wks.wikisearch.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CountryServiceImpl {
    private final CountryRepository repository;
    private final CountryCustomRepository customRepository;
    private static final String COUNTRY_PRIMARY_KEY = "countries";
    private static final String USER_PRIMARY_KEY = "users";
    public List<CountryDTOWithUsers> findAllCountries() {
        if (CacheManager.containsKey(COUNTRY_PRIMARY_KEY)) {
            return (List<CountryDTOWithUsers>) CacheManager.get(COUNTRY_PRIMARY_KEY);
        } else {
            List<Country> countries = repository.findAll();
            List<CountryDTOWithUsers> countryDTOs = new ArrayList<>();
            for(Country country : countries){
                CountryDTOWithUsers countryDTO = Conversion.convertCountryToDTOWithUsers(country);
                countryDTOs.add(countryDTO);
            }
            for (CountryDTOWithUsers country : countryDTOs){
                CacheManager.put(COUNTRY_PRIMARY_KEY + "/" + country.getName(), country);
            }
            CacheManager.put(COUNTRY_PRIMARY_KEY, countryDTOs);
            return countryDTOs;
        }
    }

    public void saveCountry(Country country) {
        try {
            repository.save(country);
            if(CacheManager.containsKey(COUNTRY_PRIMARY_KEY)) {
                List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get(COUNTRY_PRIMARY_KEY);
                countries.add(Conversion.convertCountryToDTOWithUsers(country));
                CacheManager.put(COUNTRY_PRIMARY_KEY, countries);
            }
        }
        catch (DataIntegrityViolationException ex){
            return;
        }
    }

    public CountryDTOWithUsers findByName(String name) {
        String cacheKey = COUNTRY_PRIMARY_KEY + "/" + name;
        CountryDTOWithUsers cachedValue = (CountryDTOWithUsers) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if(CacheManager.containsKey(COUNTRY_PRIMARY_KEY)) {
                List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get(COUNTRY_PRIMARY_KEY);
                Optional<CountryDTOWithUsers> findResult = countries.stream()
                        .filter(countryDTOWithUsers -> countryDTOWithUsers
                                .getName().equals(name)).findFirst();
                if(findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }

            }
            CountryDTOWithUsers result = Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(name));
            if(result != null) {
                CacheManager.put(cacheKey, result);
            }
            return result;
        }
    }

    public void deleteCountry(String name) {
        String cacheKey = COUNTRY_PRIMARY_KEY + "/" + name;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey(COUNTRY_PRIMARY_KEY)) {
            List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get(COUNTRY_PRIMARY_KEY);
            countries.removeIf(CountryDTOWithUsers -> CountryDTOWithUsers.getName().equals(name));
            CacheManager.put(COUNTRY_PRIMARY_KEY, countries);
        }
        CacheManager.remove(COUNTRY_PRIMARY_KEY);
        repository.deleteByName(name);
    }

    public void updateCountry(Country country, String countryOldName) {
        customRepository.updateCountry(country);

        CountryDTOWithUsers updateResult = Conversion.convertCountryToDTOWithUsers(repository.findCountryByName(country.getName()));
        String cacheKey = COUNTRY_PRIMARY_KEY + "/" + countryOldName;
        if (CacheManager.containsKey(cacheKey)) {
            CacheManager.remove(cacheKey);
            cacheKey = "/api/countries/" + country.getName();
            CacheManager.put(cacheKey, country);
        }

        if (CacheManager.containsKey("/api/countries")) {
            List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>) CacheManager.get("/api/countries");
            Optional<CountryDTOWithUsers> findResult = countries.stream()
                    .filter(countryDTOWithUsers -> countryDTOWithUsers
                            .getName().equals(countryOldName)).findFirst();
            CountryDTOWithUsers temp = Conversion.convertCountryToDTOWithUsers(country);
            List<UserDTO> countryUsers = findResult.get().getUsers();

            temp.setUsers(countryUsers);
            countries.removeIf(CountryDTOWithUsers -> CountryDTOWithUsers.getName().equals(countryOldName));
            countries.add(temp);
            CacheManager.put("/api/countries", countries);
        }
        CacheManager.remove("/api/users");

        for (UserDTO countryUser : updateResult.getUsers()) {
            String userKey = "/api/users/" + countryUser.getEmail();
            if (CacheManager.containsKey(userKey)) {
                UserDTOWithCountry result = (UserDTOWithCountry) CacheManager.get(userKey);
                result.setCountry(Conversion.convertCountryToDTO(country));
                CacheManager.put(userKey, result);
            }
        }

    }
}
