package com.jeanbarcellos.architecture.scanner;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Fábrica responsável por criar o
 * ClassLoader contendo todas as classes
 * e dependências do projeto validado.
 *
 * <p>
 * Este ClassLoader é utilizado pelos
 * scanners para localizar implementações
 * dos contratos arquiteturais.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ProjectClassLoaderFactory {

    private ProjectClassLoaderFactory() {
    }

    /**
     * Cria o ClassLoader do projeto.
     *
     * @param project projeto Maven atual
     *
     * @return class loader configurado
     */
    public static ClassLoader create(MavenProject project) {
        try {
            List<URL> urls = new ArrayList<>();

            for (String element : project.getCompileClasspathElements()) {
                urls.add(Path.of(element).toUri().toURL());
            }

            return new URLClassLoader(
                    urls.toArray(URL[]::new),
                    Thread.currentThread().getContextClassLoader());

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Error creating project classloader",
                    ex);
        }
    }

}