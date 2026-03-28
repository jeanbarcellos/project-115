package com.jeanbarcellos.project115.infra.adapter;

import java.util.HashMap;
import java.util.Map;

import com.jeanbarcellos.core.error.ApiError;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuarkusProblemMapper {

    public static Response toResponse(ApiError error) {

        Map<String, Object> body = new HashMap<>();
        body.put("type", error.type().toString());
        body.put("title", error.title());
        body.put("status", error.status());
        body.put("detail", error.detail());
        body.put("instance", error.instance().toString());
        body.putAll(error.properties());

        return Response.status(error.status())
                .entity(body)
                .type("application/problem+json")
                .build();
    }
}