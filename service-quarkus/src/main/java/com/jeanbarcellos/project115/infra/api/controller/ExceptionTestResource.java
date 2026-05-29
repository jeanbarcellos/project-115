package com.jeanbarcellos.project115.infra.api.controller;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.jeanbarcellos.core.error.DomainViolation;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.DomainValidationException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.project115.user.error.UserErrorType;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test/exceptions")
@Tag(name = "test-exceptions")
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionTestResource {

    @GET
    @Path("/domain")
    public void domain() {

        throw new DomainException(
                "Wallet not found");
    }

    @GET
    @Path("/domain/context")
    public void domainContext() {

        throw new DomainException(
                "Wallet not found",
                Map.of("walletId", 777));
    }

    @GET
    @Path("/domain/validation")
    public void domainValidation() {

        throw new DomainValidationException(
                "Invalid wallet",
                List.of(
                        new DomainViolation(
                                "balance",
                                "must be greater than zero",
                                -1)));
    }

    @GET
    @Path("/business")
    public void business() {

        throw new BusinessException(
                UserErrorType.USER_NOT_FOUND,
                "User not found");
    }

    @GET
    @Path("/validation")
    public void validation() {

        throw new ValidationException(
                "Validation failed",
                List.of(
                        ValidationError.of(
                                "cpf",
                                "Invalid CPF",
                                "123")));
    }

    @GET
    @Path("/runtime")
    public void runtime() {

        throw new RuntimeException("Unexpected error");
    }
}