package com.jeanbarcellos.project115.user.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.user.application.dto.UserCreateRequest;
import com.jeanbarcellos.project115.user.application.dto.UserResponse;
import com.jeanbarcellos.project115.user.application.dto.UserUpdateRequest;
import com.jeanbarcellos.project115.user.application.mapper.UserMapper;
import com.jeanbarcellos.project115.user.application.repository.UserRepository;
import com.jeanbarcellos.project115.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponse findById(Long id) {
        User user = this.repository.findById(id)
                .orElseThrow(() -> new DomainException(
                        BusinessErrorType.USER_NOT_FOUND,
                        "User with id " + id + " does not exist",
                        Map.of("userId", id)));

        return mapper.toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new DomainException(
                    BusinessErrorType.EMAIL_ALREADY_EXISTS,
                    "Email already registered",
                    Map.of("email", request.getEmail()));
        });

        User user = mapper.toEntity(request);

        this.repository.save(user);

        return mapper.toResponse(user);
    }

    public UserResponse update(Long id, UserUpdateRequest request) {

        User user = repository.findById(id)
                .orElseThrow(() -> new DomainException(
                        BusinessErrorType.USER_NOT_FOUND,
                        "User not found"));

        mapper.updateEntity(user, request);

        User updated = repository.save(user);

        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        this.repository.deleteById(id);
    }

}
