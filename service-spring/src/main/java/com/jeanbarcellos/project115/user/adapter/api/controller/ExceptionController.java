package com.jeanbarcellos.project115.user.adapter.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.project115.user.application.error.UserErrorType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/exceptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExceptionController {

    @GetMapping("/application-exception")
    public void testApplicationException() {
        throw new ApplicationException(
                "Erro ao processar tal coisa");
    }

    @GetMapping("/domain-exception")
    public void testDomainException() {
        throw new DomainException(
                UserErrorType.USER_NOT_FOUND,
                "User with id 777 does not exist",
                Map.of("userId", 777));
    }

    @GetMapping("/business-exception")
    public void testBusinessException() {
        throw new BusinessException(
                UserErrorType.USER_NOT_FOUND,
                "Details of business exception",
                Map.of("field", 777));
    }

    @GetMapping("/validation-exception")
    public void testValidationException() {
        // Arrange
        String mensagem = "Os dados da requisição são inválidos.";
        List<ValidationError> erros = List.of(
                ValidationError.of("usuario.cpf", "O CPF informado é inválido", "12345"),
                ValidationError.of("arquivo", "O tamanho excede o limite permitido"));

        throw new ValidationException(mensagem, erros);
    }

}
