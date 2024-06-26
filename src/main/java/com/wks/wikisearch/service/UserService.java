package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.UserCustomRepository;
import com.wks.wikisearch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final UserCustomRepository userCustomRepository;
    private final CountryRepository countryRepository;

    public List<UserDTOWithCountry> findAllUsers() {
        return repository.findAll().stream()
                .map(Conversion::convertAppUserWithCountry)
                .toList();
    }


    public void saveUserWithCountry(final User user, final String countryName) {
        if (!repository.existsByEmail(user.getEmail()) && countryRepository.existsByName(countryName)) {
            Country country = countryRepository.findCountryByName(countryName);
            user.setCountry(country);
            repository.save(user);
        } else {
            throw new ObjectAlreadyExistsException("User with this email already exists");
        }
    }


    public UserDTOWithCountry findByEmail(final String email) {
        if (repository.existsByEmail(email)) {
            return Conversion.convertAppUserWithCountry(
                    repository.findUserByEmail(email));
        } else {
            throw new ObjectNotFoundException("Requested non-existent user");
        }
    }

    public void updateUser(final User user) {
        repository.findById(user.getId())
                .ifPresentOrElse(
                        userToUpdate -> {
                            if (user.getCountry() != null) {
                                user.setCountry(countryRepository
                                        .findCountryByName(user.getCountry().getName()));
                            }
                            if (!Objects.equals(userToUpdate.getEmail(), user.getEmail())
                                    && repository.existsByEmail(user.getEmail())) {
                                throw new ObjectAlreadyExistsException("User with this email already registered.");
                            }
                            repository.save(user);
                        },
                        () -> {
                            throw new ObjectNotFoundException("Cannot update non-existent user");
                        }
                );
    }


    public void deleteUser(final String email) {
        if (!repository.existsByEmail(email)) {
            throw new ObjectNotFoundException("Error deleting non-existent user");
        }
        repository.deleteByEmail(email);
    }
}
