package com.jeanbarcellos.architecture.scanner;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Cria ClassLoader contendo classes e dependências
 * do projeto que está utilizando o plugin.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ProjectClassLoaderFactory {

    private ProjectClassLoaderFactory() {
    }

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