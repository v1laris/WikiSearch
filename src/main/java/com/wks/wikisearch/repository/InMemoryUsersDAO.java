package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.AppUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Repository
public class InMemoryUsersDAO {
    private final List<AppUser> USERS = new ArrayList<>();

    public List<AppUser> findAllUsers() {
        return USERS;
        /*return List.of(
                User.builder().firstName("John").email("nillkiggers@gmail.com").age(25).build(),
                User.builder().firstName("Elizabeth").email("natehiggers@gmail.com").age(22).build(),
                User.builder().firstName("Paul").email("nuckfiggers@gmail.com").age(17).build()
        );*/
    }
    public String saveUser(AppUser user) {
        USERS.add(user);
        return "User successfully added.";
    }
    public AppUser findByEmail(String email) {
        return USERS.stream()
                .filter(element -> element.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    public String updateUser(AppUser user) {
        var userIndex = IntStream.range(0, USERS.size())
                .filter(index -> USERS.get(index).getEmail().equals(user.getEmail()))
                .findFirst()
                .orElse(-1);
        if(userIndex > -1){
            USERS.set(userIndex, user);
            return "User updated.";
        }
        return("Failed to update student.");
    }

    public void deleteUser(String email) {
        var user = findByEmail(email);
        if(user != null){
            USERS.remove(user);
        }
    }
}
