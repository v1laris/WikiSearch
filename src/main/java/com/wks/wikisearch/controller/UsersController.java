package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<String> saveUser(@Valid @RequestBody final User user,
                         @Valid @PathVariable final String countryName) {
        service.saveUserWithCountry(user, countryName);
        return new ResponseEntity<>("User saved successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTOWithCountry> findByEmail(@Valid @PathVariable final String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping("/update_user")
    public ResponseEntity<String> updateUser(@Valid @RequestBody final User user) {
        service.updateUser(user);
        return new ResponseEntity<>("User updated successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete_user/{email}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable final String email) {
        service.deleteUser(email);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.CREATED);

    }

}
