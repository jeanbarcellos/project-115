package com.jeanbarcellos.project115.user.controller;

import java.util.Map;

import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.exception.DomainException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/exceptions")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@RequiredArgsConstructor
public class ExceptionController {

    @GET
    @Path("/domain-exception")
    public void testDomainException() {
        // throw new DomainException(
        // BusinessErrorType.INSUFFICIENT_BALANCE,
        // "Details ...");

        throw new DomainException(
                BusinessErrorType.INSUFFICIENT_BALANCE,
                "Balance 100.00 is lower than required 250.00",
                Map.of(
                        "balance", 100.00,
                        "required", 250.00,
                        "currency", "BRL"));
    }

}
