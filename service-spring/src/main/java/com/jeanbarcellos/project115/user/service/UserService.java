package com.jeanbarcellos.project115.user.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.user.dto.UserCreateRequest;
import com.jeanbarcellos.project115.user.dto.UserResponse;
import com.jeanbarcellos.project115.user.exception.UserNotFoundException;
import com.jeanbarcellos.project115.user.model.User;
import com.jeanbarcellos.project115.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User findById2(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " dows not exist"));
    }

    public User findById(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new DomainException(
                        BusinessErrorType.USER_NOT_FOUND,
                        "User with id " + id + " does not exist",
                        Map.of("userId", id)));
    }

    public UserResponse create(UserCreateRequest request) {
        var user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        repository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new DomainException(
                    BusinessErrorType.EMAIL_ALREADY_EXISTS,
                    "Email already registered",
                    Map.of("email", user.getEmail()));
        });

        this.repository.save(user);

        return UserResponse.from(user);
    }

    public void delete(Long id) {
        this.repository.deleteById(id);
    }

}
