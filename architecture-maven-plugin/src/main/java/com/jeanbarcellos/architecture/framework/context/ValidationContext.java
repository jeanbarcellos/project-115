package com.jeanbarcellos.architecture.framework.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.framework.cache.ScanCache;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.report.ValidationReport;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;

import lombok.Getter;

/**
 * Contexto compartilhado entre todas as regras
 * executadas durante o processo de validação.
 *
 * <p>
 * Permite compartilhar informações entre
 * diferentes validadores sem acoplamento direto.
 * </p>
 *
 * <p>
 * Atualmente é utilizado para controle
 * de unicidade global dos códigos de erro.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
public class ValidationContext {

    private final MavenProject project;

    private final ClassLoader classLoader;

    private final ScanCache scanCache = new ScanCache();

    /**
     * Controle de unicidade global dos códigos.
     */
    private final Set<String> errorCodes = new HashSet<>();

    /**
     * Relatório acumulado.
     */
    private final ValidationReport report = new ValidationReport();

    /**
     * Registries utilizados pelas regras para controle de unicidade e estado
     * temporário durante a validação.
     */
    private final Map<String, Set<String>> registries = new HashMap<>();

    /**
     * Cria contexto.
     *
     * @param project     projeto Maven
     * @param classLoader classloader do projeto
     */
    public ValidationContext(MavenProject project, ClassLoader classLoader) {
        this.project = project;
        this.classLoader = classLoader;
    }

    /**
     * Registra um valor único.
     *
     * @param registry nome do registry
     * @param value    valor registrado
     *
     * @return true quando o valor ainda não havia sido registrado
     */
    public boolean registerUnique(String registry, String value) {

        return registries
                .computeIfAbsent(registry, key -> new HashSet<>())
                .add(value);
    }

    /**
     * Registra violação relacionada a uma classe.
     *
     * @param module   módulo de governança
     * @param category categoria da validação
     * @param rule     regra responsável
     * @param clazz    classe relacionada
     * @param message  descrição da violação
     */
    public void addViolation(
            ValidationModule module,
            ValidationCategory category,
            ValidationRule rule,
            Class<?> clazz,
            String message) {

        this.report.addViolation(
                module,
                category,
                rule,
                clazz.getName(),
                null,
                null,
                null,
                null,
                message);
    }

    /**
     * Registra violação relacionada a um elemento específico.
     *
     * @param module   módulo de governança
     * @param category categoria da validação
     * @param rule     regra responsável
     * @param clazz    classe relacionada
     * @param element  elemento específico
     * @param message  descrição da violação
     */
    public void addViolation(
            ValidationModule module,
            ValidationCategory category,
            ValidationRule rule,
            Class<?> clazz,
            String element,
            String message) {

        this.report.addViolation(
                module,
                category,
                rule,
                clazz.getName(),
                element,
                null,
                null,
                null,
                message);
    }
}