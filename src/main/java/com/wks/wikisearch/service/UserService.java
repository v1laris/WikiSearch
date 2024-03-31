package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.exception.ResourceAlreadyExistsException;
import com.wks.wikisearch.exception.ResourceNotFoundException;
import com.wks.wikisearch.model.CachePrimaryKeys;
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
import java.util.Objects;
import java.util.Optional;

import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final UserCustomRepository userCustomRepository;
    private final CountryRepository countryRepository;
    private final CacheService cacheService;

    public List<UserDTOWithCountry> findAllUsers() {
        if (CacheManager.containsKey(CachePrimaryKeys.USER_PRIMARY_KEY)) {
            return (List<UserDTOWithCountry>) CacheManager.get(CachePrimaryKeys.USER_PRIMARY_KEY);

        } else {
            List<User> users = repository.findAll();
            List<UserDTOWithCountry> appUserDTOs = new ArrayList<>();
            for (User user : users) {
                UserDTOWithCountry appUserDTO = Conversion.convertAppUserWithCountry(user);
                appUserDTOs.add(appUserDTO);
            }
            for (UserDTOWithCountry user : appUserDTOs) {
                CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY + user.getEmail(), user);
            }
            CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY, appUserDTOs);
            return appUserDTOs;
        }
    }

    public List<UserDTOWithCountry> findUsersByDateOfBirth(final Integer startYear, final Integer endYear) {
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

    public void saveUserWithCountry(final User user, final String countryName) {
        if (!repository.existsByEmail(user.getEmail()) && countryRepository.existsByName(countryName)) {
            Country country = countryRepository.findCountryByName(countryName);
            user.setCountry(country);
            repository.save(user);
            if (CacheManager.containsKey(CachePrimaryKeys.USER_PRIMARY_KEY)) {
                List<UserDTOWithCountry> users =
                        (List<UserDTOWithCountry>)
                                CacheManager.get(CachePrimaryKeys.USER_PRIMARY_KEY);
                users.add(Conversion.convertAppUserWithCountry(user));
                CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY, users);
            }
            if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + countryName)) {
                CacheManager.remove(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + countryName);
                CacheManager.remove(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
            }
        } else {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }
    }


    public UserDTOWithCountry findByEmail(final String email) {
        UserDTOWithCountry user = cacheService.getUser(email);
        if (user != null) {
            return user;
        } else {
            if (repository.existsByEmail(email)) {
                UserDTOWithCountry result =
                        Conversion.convertAppUserWithCountry(
                                repository.findUserByEmail(email));
                cacheService.addUser(result);
                return result;
            } else {
                throw new ResourceNotFoundException("Requested non-existent user");
            }
        }
    }

    public void updateUser(final User user) {
        Optional<User> temp = repository.findById(user.getId());
        if (temp.isPresent()) {
            User userToUpdate = temp.get();
            user.setCountry(countryRepository.findCountryByName(user.getCountry().getName()));
            if (!Objects.equals(userToUpdate.getEmail(), user.getEmail()) && repository.existsByEmail(user.getEmail())) {
                throw new ResourceAlreadyExistsException("User with this email already registered.");
            }
            //
            cacheService.updateUser(userToUpdate, user);
            userCustomRepository.updateUser(userToUpdate);
        }
    }

    public void deleteUser(final String email) {
        if (!repository.existsByEmail(email)) {
            throw new ResourceNotFoundException("Error deleting non-existent user");
        }
        cacheService.removeUser(email);
        repository.deleteByEmail(email);
    }
}
