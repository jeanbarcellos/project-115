package com.jeanbarcellos.core.spring.config;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import com.jeanbarcellos.core.spring.aop.ValidationAspect;
import com.jeanbarcellos.core.validation.Validator;

/**
 * Auto-configuração responsável por instanciar e expor o validador customizado da biblioteca
 * Cadastro Core como um Bean do Spring.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@AutoConfiguration
public class ValidationAutoConfiguration {

    /**
     * Registra o validador customizado no contexto do Spring.
     *
     * @param jakartaValidator motor de validação já provido pelo Spring Boot
     * @return instância configurada do nosso Validator
     */
    @Bean
    @ConditionalOnMissingBean
    Validator customCadastroValidator(jakarta.validation.Validator jakartaValidator) {
        return new Validator(jakartaValidator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationAspect validationAspect(Validator customCadastroValidator) {
        return new ValidationAspect(customCadastroValidator);
    }

}
