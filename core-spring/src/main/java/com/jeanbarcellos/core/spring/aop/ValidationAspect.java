package com.jeanbarcellos.core.spring.aop;

import java.lang.annotation.Annotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import com.jeanbarcellos.core.spring.annotation.Valid;
import com.jeanbarcellos.core.validation.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Interceptador AOP responsável por capturar parâmetros anotados com @Valid e disparar o Validator
 * do Core antes da execução do método.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Aspect
@RequiredArgsConstructor
public class ValidationAspect {

    private final Validator validator;

    /**
     * Pointcut 1: Validação de Parâmetros. Intercepta qualquer método que possua ao menos um
     * parâmetro anotado com @Valid.
     */
    @Before("execution(* *(.., @com.jeanbarcellos.core.spring.annotation.Valid (*), ..))")
    public void validateParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        for (int i = 0; i < args.length; i++) {
            Object currentArg = args[i];

            if (currentArg != null && hasCadastroValidAnnotation(parameterAnnotations[i])) {
                // Valida o parâmetro de entrada
                this.validator.validate(currentArg);
            }
        }
    }

    /**
     * Pointcut 2: Validação de Retorno (METHOD). Intercepta a saída de métodos que estejam anotados
     * com @Valid.
     */
    @AfterReturning(pointcut = "@annotation(com.jeanbarcellos.core.spring.annotation.Valid)", returning = "result")
    public void validateMethodReturn(Object result) {
        if (result != null) {
            // Valida o objeto que o método acabou de devolver
            this.validator.validate(result);
        }
    }

    /**
     * Verifica se o array de anotações de um parâmetro contém o @Valid.
     */
    private boolean hasCadastroValidAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Valid) {
                return true;
            }
        }
        return false;
    }
}
