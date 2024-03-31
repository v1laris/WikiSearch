package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {
    private final UserService service;

    @GetMapping
    public List<UserDTOWithCountry> findAllUsers() {
        return service.findAllUsers();
    }

    @PostMapping("/save_user/{countryName}")
    public ResponseEntity<String> saveUser(@RequestBody final User user,
                         @PathVariable final String countryName) {
        service.saveUserWithCountry(user, countryName);
        return new ResponseEntity<>("User saved successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTOWithCountry> findByEmail(@PathVariable final String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping("/update_user")
    public ResponseEntity<String> updateUser(@RequestBody final User user) {
        service.updateUser(user);
        return new ResponseEntity<>("User updated successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete_user/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable final String email) {
        service.deleteUser(email);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.CREATED);

    }

    @GetMapping("/by_date_of_birth")
    public List<UserDTOWithCountry> getUsersByDateOfBirth(
            @RequestParam final int startYear,
            @RequestParam final int endYear) {
        return service.findUsersByDateOfBirth(startYear, endYear);
    }
}
