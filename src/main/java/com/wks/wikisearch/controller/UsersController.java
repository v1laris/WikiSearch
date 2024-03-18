package com.wks.wikisearch.controller;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.service.Conversion;
import com.wks.wikisearch.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {
    private final UserServiceImpl service;
    @GetMapping
    public List<UserDTOWithCountry> findAllUsers() {
        return service.findAllUsers();
    }

    @PostMapping("save_user/{countryName}")
    public void saveUser(@RequestBody User user, @PathVariable String countryName) {
        service.saveUserWithCountry(user, countryName);
    }

    @GetMapping("/{email}")
    public UserDTOWithCountry findByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @PutMapping("update_user")
    public void updateUser(@RequestBody User user) {
        service.updateUser(user);
    }

    @DeleteMapping("delete_user/{email}")
    public void deleteUser(@PathVariable String email) {
        service.deleteUser(email);
    }

    @GetMapping("/by_date_of_birth")
    public List<UserDTOWithCountry> getUsersByDateOfBirth(@RequestParam int startYear, @RequestParam int endYear) {
        String cacheKey = "/api/users/" + startYear + "-" + endYear;
        if (CacheManager.containsKey(cacheKey)) {
            return (List<UserDTOWithCountry>)CacheManager.get(cacheKey);

        } else {
            List<UserDTOWithCountry> result = service.findUsersByDateOfBirth(startYear, endYear);
            CacheManager.put(cacheKey, result);
            return result;
        }
    }
}
