package com.curso.diccionario.api.contrato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * El "Dado que..." de las pruebas de contrato: qué diccionarios, palabras y significados
 * existen. Cada implementación sabe cómo materializarlo (ficheros, BBDD...).
 */
public final class DatosDePrueba {

    private final Map<String, Map<String, List<SignificadoDePrueba>>> diccionarios = new LinkedHashMap<>();

    public DatosDePrueba conDiccionario(String idioma) {
        diccionarios.putIfAbsent(idioma, new LinkedHashMap<>());
        return this;
    }

    public DatosDePrueba conPalabra(String idioma, String palabra, SignificadoDePrueba primero, SignificadoDePrueba... resto) {
        // El primer significado va aparte para que no se pueda describir una palabra sin
        // significados: el API no tiene forma de representarla.
        List<SignificadoDePrueba> significados = new ArrayList<>();
        significados.add(primero);
        significados.addAll(List.of(resto));
        conDiccionario(idioma);
        diccionarios.get(idioma).put(palabra, List.copyOf(significados));
        return this;
    }

    public Map<String, Map<String, List<SignificadoDePrueba>>> getDiccionarios() {
        return Collections.unmodifiableMap(diccionarios);
    }

}
