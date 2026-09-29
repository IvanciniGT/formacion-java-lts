package com.curso.diccionario.impl.ficheros;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;

/**
 * Lleva el "Dado que..." de las pruebas de contrato a disco, en el formato que leerá la implementación:
 *
 *     <carpeta>/<idioma>.txt                                un fichero por idioma, en minúsculas: ES -> es.txt
 *                                                           (vacío = diccionario sin palabras)
 *     palabra=significado1(ej1)(ej2)|significado2(ej1)      una línea por palabra, significados en orden
 *
 * Los caracteres = | ( ) y el salto de línea están prohibidos en palabras, significados y ejemplos:
 * el formato no los escapa (por ahora), y un fichero ambiguo haría que la prueba mintiera.
 */
final class FicherosDePrueba {

    private static final String RESERVADOS = "=|()\n\r";

    private FicherosDePrueba() {
    }

    static void escribir(DatosDePrueba datos, Path carpeta) {
        for (Map.Entry<String, Map<String, List<SignificadoDePrueba>>> diccionario : datos.getDiccionarios().entrySet()) {
            List<String> lineas = new ArrayList<>();
            diccionario.getValue().forEach((palabra, significados) -> {
                comprobarSinReservados(palabra);
                List<String> textos = new ArrayList<>();
                for (SignificadoDePrueba significado : significados) {
                    comprobarSinReservados(significado.texto());
                    significado.ejemplos().forEach(FicherosDePrueba::comprobarSinReservados);
                    StringBuilder texto = new StringBuilder(significado.texto());
                    significado.ejemplos().forEach(ejemplo -> texto.append('(').append(ejemplo).append(')'));
                    textos.add(texto.toString());
                }
                lineas.add(palabra + "=" + String.join("|", textos));
            });
            try {
                // Locale.ROOT: con el locale turco, "I".toLowerCase() no da "i".
                String fichero = diccionario.getKey().toLowerCase(Locale.ROOT) + ".txt";
                Files.write(carpeta.resolve(fichero), lineas, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static void comprobarSinReservados(String texto) {
        for (char caracter : texto.toCharArray()) {
            if (RESERVADOS.indexOf(caracter) >= 0) {
                throw new IllegalArgumentException("El formato de fichero no admite '" + caracter + "': " + texto);
            }
        }
    }

}
