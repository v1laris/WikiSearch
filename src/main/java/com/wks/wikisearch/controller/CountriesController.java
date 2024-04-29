package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.service.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
@CrossOrigin
public class CountriesController {
    private final CountryService service;

    @GetMapping
    public List<CountryDTOWithUsers> findAllCountries() {
        return service.findAllCountries();
    }

    @PostMapping("save_country")
    public ResponseEntity<String> saveCountry(@RequestBody final Country country) {
        service.saveCountry(country);
        return new ResponseEntity<>("Country saved successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<CountryDTOWithUsers> findByName(@PathVariable final String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @DeleteMapping("delete_country/{name}")
    public ResponseEntity<String> deleteCountry(@PathVariable final String name) {
        service.deleteCountry(name);
        return new ResponseEntity<>("Country deleted successfully", HttpStatus.OK);
    }

    @PutMapping("update_country/{countryOldName}")
    public ResponseEntity<Map<String, String>> updateCountry(
            @PathVariable final String countryOldName,
            @RequestBody final Country country) {
        service.updateCountry(country, countryOldName);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Country updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
