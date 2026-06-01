package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;

/**
 * Contrato base para regras arquiteturais.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ValidationRule<T> {

    void validate(T target, ValidationContext context) throws MojoExecutionException;
}