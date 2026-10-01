package com.curso.diccionario.impl.ficheros.formato;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Dentro de un jar no se puede listar una carpeta, solo pedir un recurso por su nombre.
// Por eso cada jar de diccionarios lleva un índice con un nombre fijo (<carpeta>idiomas, un código por línea),
// y getResources -en plural- devuelve los de todos los jars: los idiomas disponibles son, por
// construcción, los jars que hay. Nada que configurar ni que se pueda desincronizar.
//
// En META-INF porque no es un paquete: en el module path no está encapsulado (no hace falta opens)
// y dos jars pueden tener la misma carpeta sin que sea un paquete partido entre módulos.
public final class CatalogoDeDiccionariosEnClasspath {

    public static final String CARPETA_POR_DEFECTO = "META-INF/diccionarios/";
    public static final String NOMBRE_DEL_INDICE = "idiomas";
    private static final String EXTENSION = ".txt";

    private final ClassLoader classLoader;
    private final String carpeta;

    public CatalogoDeDiccionariosEnClasspath(ClassLoader classLoader, String carpeta) {
        this.classLoader = classLoader;
        this.carpeta = carpeta.endsWith("/") ? carpeta : carpeta + "/";
    }

    public CatalogoDeDiccionariosEnClasspath(ClassLoader classLoader) {
        this(classLoader, CARPETA_POR_DEFECTO);
    }

    // Solo los que además tienen fichero: un índice que anuncia un idioma sin su .txt es un jar mal hecho.
    public List<String> idiomas() throws IOException {
        try {
            return Collections.list(classLoader.getResources(carpeta + NOMBRE_DEL_INDICE)).stream()
                    .flatMap(indice -> lineasDe(indice).stream())
                    .map(String::trim)
                    .filter(linea -> !linea.isEmpty())
                    .map(idioma -> idioma.toUpperCase(Locale.ROOT))
                    .distinct()
                    .filter(idioma -> fichero(idioma).isPresent())
                    .sorted()
                    .toList();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    // El class loader y no una clase concreta: el jar con los diccionarios es otro módulo.
    public Optional<URL> fichero(String idioma) {
        // Locale.ROOT: con el locale turco, "I".toLowerCase() no da "i".
        return Optional.ofNullable(classLoader.getResource(carpeta + idioma.toLowerCase(Locale.ROOT) + EXTENSION));
    }

    private static List<String> lineasDe(URL indice) {
        try {
            return new String(FormatoDeFicheroDeDiccionario.leerBytes(indice), StandardCharsets.UTF_8).lines().toList();
        } catch (IOException e) {
            // Dentro de un stream no se puede lanzar una comprobada; idiomas() la desenvuelve.
            throw new UncheckedIOException(e);
        }
    }
}
