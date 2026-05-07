package com.jeanbarcellos.project115.infra.adapter;

import java.util.HashMap;
import java.util.Map;

import com.jeanbarcellos.core.error.ErrorResponse;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuarkusProblemMapper {

    public static Response toResponse(ErrorResponse error) {

        Map<String, Object> body = new HashMap<>();
        body.put("type", error.getType().toString());
        body.put("title", error.getTitle());
        body.put("status", error.getStatus());
        body.put("detail", error.getDetail());
        body.put("instance", error.getInstance().toString());
        body.putAll(error.getProperties());

        return Response.status(error.getStatus())
                .entity(body)
                .type("application/problem+json")
                .build();
    }
}