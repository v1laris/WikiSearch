package com.wks.wikisearch.controller;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.servise.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UsersController {
    private final AppUserService service;
    @GetMapping
    public List<AppUser> findAllUsers() {

        return service.findAllUsers();
    }

    @PostMapping("save_user")
    public String saveStudent(@RequestBody AppUser student) {
        service.saveUser(student);
        return "Student successfully saved";
    }

    @GetMapping("/{email}")
    public AppUser findByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }
    // /api/v1/students/oleg12@gmail.com

    @PutMapping("update_user")
    public String updateUser(@RequestBody AppUser student) {
        return service.updateUser(student);
    }

    @DeleteMapping("delete_user/{email}")
    public void deleteUser(@PathVariable String email) {
        service.deleteUser(email);
    }
}
