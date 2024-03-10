package com.wks.wikisearch.servise;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.AppUserRepository;
import com.wks.wikisearch.servise.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Primary
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final CountryRepository countryRepository;
    @Override
    public List<AppUser> findAllUsers() {
        return repository.findAll();
    }


    public AppUser saveUserWithCountry(AppUser user, String countryName) {
        Country country = countryRepository.findCountryByName(countryName);
        user.setCountry(country);
        return repository.save(user);
    }

    @Override
    public String saveUser(AppUser user) {
        repository.save(user);
        return "User saved.";
    }

    @Override
    public AppUser findByEmail(String email) {
        return repository.findAppUserByEmail(email);
    }

    @Override
    public String updateUser(AppUser user) {
        repository.save(user);
        return "User updated.";
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        repository.deleteByEmail(email);
    }
}
