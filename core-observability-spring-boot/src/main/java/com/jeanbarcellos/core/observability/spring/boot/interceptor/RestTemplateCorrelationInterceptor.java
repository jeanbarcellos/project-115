package com.jeanbarcellos.core.observability.spring.boot.interceptor;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;

public class RestTemplateCorrelationInterceptor  { // implements ClientHttpRequestInterceptor

    // private final ObservabilityProperties properties;

    // private final ObservabilityContextHolder contextHolder;

    // public RestTemplateCorrelationInterceptor(
    //         ObservabilityProperties properties,
    //         ObservabilityContextHolder contextHolder) {

    //     this.properties = properties;
    //     this.contextHolder = contextHolder;
    // }

    // @Override
    // public ClientHttpResponse intercept(
    //         HttpRequest request,
    //         byte[] body,
    //         ClientHttpRequestExecution execution)
    //         throws IOException {

    //     String correlationId = contextHolder.getCurrentContext().getCorrelationId();

    //     if (correlationId != null) {
    //         request.getHeaders().set(properties.getCorrelationHeader(),correlationId);
    //     }

    //     return execution.execute(request, body);
    // }

}