package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.dto.UserDTO;
import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.service.Conversion;
import com.wks.wikisearch.service.CountryServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountriesController {
    private final CountryServiceImpl service;

    @GetMapping
    public List<CountryDTOWithUsers> findAllCountries() {
        String cacheKey = "/api/countries";
        if (CacheManager.containsKey(cacheKey)) {
            return (List<CountryDTOWithUsers>) CacheManager.get(cacheKey);
        } else {
            List<CountryDTOWithUsers> result = service.findAllCountries();
            for (CountryDTOWithUsers country : result){
                CacheManager.put("/api/countries/" + country.getName(), country);
            }
            CacheManager.put(cacheKey, result);
            return result;
        }
    }

    @PostMapping("save_country")
    public void saveCountry(@RequestBody Country country) {
        Country isSaved = service.saveCountry(country);
        if(isSaved != null) {
            if(CacheManager.containsKey("/api/countries")) {
                List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get("/api/countries");
                countries.add(Conversion.convertCountryToDTOWithUsers(country));
                CacheManager.put("/api/countries", countries);
            }
        }
    }

    @GetMapping("/{name}")
    public CountryDTOWithUsers findByName(@PathVariable String name) {
        String cacheKey = "/api/countries/" + name;
        CountryDTOWithUsers cachedValue = (CountryDTOWithUsers) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if(CacheManager.containsKey("/api/countries")) {
                List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get("/api/countries");
                Optional<CountryDTOWithUsers> findResult = countries.stream()
                        .filter(countryDTOWithUsers -> countryDTOWithUsers
                                .getName().equals(name)).findFirst();
                if(findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
                else {
                    CountryDTOWithUsers result = service.findByName(name);
                    CacheManager.put(cacheKey, result);
                    return result;
                }

            } else {
                CountryDTOWithUsers result = service.findByName(name);
                CacheManager.put(cacheKey, result);
                return result;
            }
        }
    }

    @DeleteMapping("delete_country/{name}")
    public void deleteCountry(@PathVariable String name) {
        String cacheKey = "/api/countries/" + name;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey("/api/countries")) {
            List<CountryDTOWithUsers> countries = (List<CountryDTOWithUsers>)CacheManager.get("/api/countries");
            countries.removeIf(CountryDTOWithUsers -> CountryDTOWithUsers.getName().equals(name));
            CacheManager.put("/api/countries", countries);
        }
        CacheManager.remove("/api/users");
        service.deleteCountry(name);
    }

    @PutMapping("update_country/{countryOldName}")
    public void updateCountry(@PathVariable String countryOldName, @RequestBody Country country) {
        service.updateCountry(country);

        CountryDTOWithUsers updateResult = service.findByName(country.getName());
        String cacheKey = "/api/countries/" + countryOldName;
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
