package com.jeanbarcellos.project115.user.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.exception.DomainException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/exceptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExceptionController {

    @GetMapping("/domain-exception")
    public void testDomainException() {
        throw new DomainException(
                BusinessErrorType.INSUFFICIENT_BALANCE,
                "Details ...");
    }

}
