package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.service.CountryServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountriesController {
    private final CountryServiceImpl service;

    @GetMapping
    public List<CountryDTOWithUsers> findAllCountries() {
        return service.findAllCountries();
    }

    @PostMapping("save_country")
    public void saveCountry(@RequestBody Country country) {
        service.saveCountry(country);
    }

    @GetMapping("/{name}")
    public CountryDTOWithUsers findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @DeleteMapping("delete_country/{name}")
    public void deleteCountry(@PathVariable String name) {
        service.deleteCountry(name);
    }

    @PutMapping("update_country/{countryOldName}")
    public void updateCountry(@PathVariable String countryOldName, @RequestBody Country country) {
        service.updateCountry(country, countryOldName);
    }
}
