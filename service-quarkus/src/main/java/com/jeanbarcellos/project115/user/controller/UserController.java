package com.jeanbarcellos.project115.user.controller;

import java.net.URI;

import com.jeanbarcellos.project115.user.dto.UserCreateRequest;
import com.jeanbarcellos.project115.user.dto.UserResponse;
import com.jeanbarcellos.project115.user.model.User;
import com.jeanbarcellos.project115.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") Long id,
            @HeaderParam("If-None-Match") String ifNoneMatch) {

        User user = service.findById(id);
        String etagValue = user.getVersion().toString(); // JAX-RS lida com as aspas no EntityTag

        if (ifNoneMatch != null && ifNoneMatch.equals("\"" + etagValue + "\"")) {
            return Response.status(Status.NOT_MODIFIED)
                    .tag(new EntityTag(etagValue))
                    .build();
        }

        return Response.ok(UserResponse.from(user))
                .tag(new EntityTag(etagValue))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid UserCreateRequest request) {
        UserResponse response = service.create(request);

        URI location = URI.create("/users/" + response.getId());

        return Response.created(location)
                .entity(response)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}