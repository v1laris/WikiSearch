package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.repository.InMemoryUsersDAO;
import com.wks.wikisearch.servise.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InMemoryUsersServiceImpl implements AppUserService {
    private final InMemoryUsersDAO repository;
    @Override
    public List<AppUser> findAllUsers() {
        return repository.findAllUsers();
        /*return List.of(
                User.builder().firstName("John").email("nillkiggers@gmail.com").age(25).build(),
                User.builder().firstName("Elizabeth").email("natehiggers@gmail.com").age(22).build(),
                User.builder().firstName("Paul").email("nuckfiggers@gmail.com").age(17).build()
        );*/
    }

    @Override
    public String saveUser(AppUser user) {
        return repository.saveUser(user);
    }

    @Override
    public AppUser findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public String updateUser(AppUser user) {
        return repository.updateUser(user);
    }

    @Override
    public void deleteUser(String email) {
        repository.deleteUser(email);
    }
}
