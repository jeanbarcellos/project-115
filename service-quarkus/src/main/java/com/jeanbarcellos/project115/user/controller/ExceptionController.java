package com.jeanbarcellos.project115.user.controller;

import com.jeanbarcellos.core.apierror.BusinessErrorType;
import com.jeanbarcellos.core.apierror.DomainException;

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
        throw new DomainException(
                BusinessErrorType.INSUFFICIENT_BALANCE,
                "Details ...");
    }

}
