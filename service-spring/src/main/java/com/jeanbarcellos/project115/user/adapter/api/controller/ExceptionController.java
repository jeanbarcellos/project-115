package com.jeanbarcellos.project115.user.adapter.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.core.error.DomainViolation;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.DomainValidationException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.project115.user.application.error.UserErrorType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/exceptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExceptionController {


    // DOMAIN =================================================================

    @GetMapping("/domain-exception")
    public void testDomainException() {

        String message = "User with id 777 does not exist";

        throw new DomainException(message);
    }

    @GetMapping("/domain-exception-with-context")
    public void testDomainExceptionWithContext() {

        String message = "User with id 777 does not exist";
        Map<String, Object> context = Map.of("userId", 777);

        throw new DomainException(message, context);
    }

    @GetMapping("/domain-validation-exception")
    public void testDomainValidationException() {

        String message = "Invalid wallet creation";

        List<DomainViolation> violations = new ArrayList<>();
        violations.add(new DomainViolation("initialBalance", "must not be null", null));

        throw new DomainValidationException(message, violations);
    }

    // BUSINESS ===============================================================

    @GetMapping("/business-exception")
    public void testBusinessException() {
        throw new BusinessException(
                UserErrorType.USER_NOT_FOUND,
                "Details of business exception",
                Map.of("field", 777));
    }

    // VALIDATION → 422 =======================================================

    @GetMapping("/validation-exception")
    public void testValidationException() {
        // Arrange
        String mensagem = "Os dados da requisição são inválidos.";
        List<ValidationError> erros = List.of(
                ValidationError.of("usuario.cpf", "O CPF informado é inválido", "12345"),
                ValidationError.of("arquivo", "O tamanho excede o limite permitido"));

        throw new ValidationException(mensagem, erros);
    }

    // APPLICATION (fallback controlado) ======================================

    @GetMapping("/technical-exception")
    public void testApplicationException() {
        throw new RuntimeException("Erro ao processar tal coisa");
    }

    // GENERIC / TECHNICAL ====================================================

}
