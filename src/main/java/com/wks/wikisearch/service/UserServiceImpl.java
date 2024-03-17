package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.UserCustomRepository;
import com.wks.wikisearch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl {

    private final UserRepository repository;
    private final CountryRepository countryRepository;

    public List<UserDTOWithCountry> findAllUsers() {
        List<User> users = repository.findAll();
        List<UserDTOWithCountry> appUserDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTOWithCountry appUserDTO = Conversion.convertAppUserWithCountry(user);
            appUserDTOs.add(appUserDTO);
        }
        return appUserDTOs;
    }

    public List<UserDTOWithCountry> findUsersByDateOfBirth(Integer startYear, Integer endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);
        List<User> users = repository.findUsersByDateOfBirthBetween(startDate, endDate);
        List<UserDTOWithCountry> appUserDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTOWithCountry appUserDTO = Conversion.convertAppUserWithCountry(user);
            appUserDTOs.add(appUserDTO);
        }
        return appUserDTOs;
    }

    public User saveUserWithCountry(User user, String countryName) {
        Country country = countryRepository.findCountryByName(countryName);
        user.setCountry(country);
        return repository.save(user);
    }


    public UserDTOWithCountry findByEmail(String email) {
        if (repository.existsByEmail(email)) {
            return Conversion.convertAppUserWithCountry(repository.findUserByEmail(email));

        }
        return null;
    }


    public void updateUser(User user) {
        Optional<User> temp = repository.findById(user.getId());
        if(temp.isPresent()){
            User userToUpdate = temp.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setCountry(user.getCountry());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setDateOfBirth(user.getDateOfBirth());
            repository.save(userToUpdate);
        }
    }

    public void deleteUser(String email) {
        repository.deleteByEmail(email);
    }
}
