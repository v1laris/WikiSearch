package com.wks.wikisearch.controller;

import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.servise.impl.CountryServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
@AllArgsConstructor
public class CountriesController {
    private final CountryServiceImpl service;
    @GetMapping
    public List<Country> findAllUsers() {

        return service.findAllCountries();
    }

    @PostMapping("save_country")
    public String saveStudent(@RequestBody Country country) {
        service.saveCountry(country);
        return "Country successfully saved";
    }

    @GetMapping("/{name}")
    public Country findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PutMapping("update_country")
    public String updateCountry(@RequestBody Country country) {
        return service.updateCountry(country);
    }

    @DeleteMapping("delete_country/{name}")
    public void deleteCountry(@PathVariable String name) {
        service.deleteCountry(name);
    }
}
