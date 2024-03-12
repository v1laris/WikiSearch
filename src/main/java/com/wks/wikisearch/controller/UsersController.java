package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.AppUserDTOWithCountry;
import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.service.AppUserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {
    private final AppUserServiceImpl service;
    @GetMapping
    public List<AppUserDTOWithCountry> findAllUsers() {
        return service.findAllUsers();
    }

    @PostMapping("save_user/{countryName}")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user, @PathVariable String countryName) {
        AppUser savedUser = service.saveUserWithCountry(user, countryName);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public AppUserDTOWithCountry findByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @PutMapping("update_user")
    public String updateUser(@RequestBody AppUser user) {
        return service.updateUser(user);
    }

    @DeleteMapping("delete_user/{email}")
    public void deleteUser(@PathVariable String email) {
        service.deleteUser(email);
    }
}