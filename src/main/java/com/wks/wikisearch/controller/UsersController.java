package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.CountryDTOWithUsers;
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
        String cacheKey = "/api/users";
        if (CacheManager.containsKey(cacheKey)) {
            return (List<UserDTOWithCountry>)CacheManager.get(cacheKey);

        } else {
            List<UserDTOWithCountry> result = service.findAllUsers();
            for(UserDTOWithCountry user : result){
                CacheManager.put("/api/users/" + user.getEmail(), user);
            }
            CacheManager.put(cacheKey, result);
            return result;
        }
    }

    @PostMapping("save_user/{countryName}")
    public ResponseEntity<User> saveUser(@RequestBody User user, @PathVariable String countryName) {
        User savedUser = service.saveUserWithCountry(user, countryName);
        if(savedUser != null) {
            if(CacheManager.containsKey("/api/users")){
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get("/api/users");
                users.add(Conversion.convertAppUserWithCountry(user));
                CacheManager.put("/api/users", users);
            }
            if(CacheManager.containsKey("/api/countries/" + countryName)){
                CacheManager.remove("/api/countries/" + countryName);
                CacheManager.remove("/api/countries");
            }
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.ALREADY_REPORTED);
    }

    @GetMapping("/{email}")
    public UserDTOWithCountry findByEmail(@PathVariable String email) {
        String cacheKey = "/api/users/" + email;
        UserDTOWithCountry cachedValue = (UserDTOWithCountry) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if(CacheManager.containsKey("/api/users")){
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get("/api/users");
                Optional<UserDTOWithCountry> findResult = users.stream()
                        .filter(UserDTOWithCountry -> UserDTOWithCountry
                                .getEmail().equals(email)).findFirst();
                if(findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
                else {
                    UserDTOWithCountry result = service.findByEmail(email);
                    CacheManager.put(cacheKey, result);
                    return result;
                }
            } else {
                UserDTOWithCountry result = service.findByEmail(email);
                CacheManager.put(cacheKey, result);
                return result;
            }
        }
    }

    @PutMapping("update_user")
    public void updateUser(@RequestBody User user) {
        service.updateUser(user);
            String cacheKey = "/api/users/" + user.getEmail();
            if (CacheManager.containsKey(cacheKey)) {
                CacheManager.remove(cacheKey);
                CacheManager.put(cacheKey, user);
            }
            if (CacheManager.containsKey("/api/countries" + user.getCountry().getName())) {
                CacheManager.remove("/api/countries" + user.getCountry().getName());
            }

            if (CacheManager.containsKey("/api/users")) {
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>) CacheManager.get("/api/users");
                Optional<UserDTOWithCountry> findResult = users.stream()
                        .filter(UserDTOWithCountry -> UserDTOWithCountry
                                .getEmail().equals(user.getEmail())).findFirst();
                users.removeIf(UserDTOWithCountry -> UserDTOWithCountry.getEmail().equals(user.getEmail()));
                users.add(Conversion.convertAppUserWithCountry(user));
                CacheManager.put("/api/countries", users);
            }
            if (CacheManager.get(cacheKey) != null) {
                CacheManager.put(cacheKey, user);
                CacheManager.remove("/api/users");
            }
    }

    @DeleteMapping("delete_user/{email}")
    public void deleteUser(@PathVariable String email) {
        String cacheKey = "/api/users/" + email;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey("/api/users")){
            List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get("/api/users");
            users.removeIf(UserDTOWithCountry -> UserDTOWithCountry.getEmail().equals(email));
            CacheManager.put("/api/users", users);
        }
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
