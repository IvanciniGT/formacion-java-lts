package com.curso.diccionario.impl.ficheros;

import static com.curso.diccionario.api.contrato.SignificadoDePrueba.significado;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.curso.diccionario.api.contrato.ContratoBase;
import com.curso.diccionario.api.contrato.DatosDePrueba;

/**
 * Las pruebas de contrato leen ficheros generados por FicherosDePrueba. Si el generador y el lector
 * entendieran mal el formato de la misma manera, pasarían igual: por eso el generador se compara
 * con un fichero escrito a mano, como lo escribiría un lingüista.
 */
class FicherosDePruebaTest {

    @TempDir
    Path carpeta;

    @Test
    @DisplayName("Contexto: los datos habituales y META-INF/diccionarios/es.txt escrito a mano | Acción: generar los ficheros | Resultado esperado: el es.txt generado es idéntico al escrito a mano")
    void elGeneradorEscribeElFormatoReal() throws IOException {
        // Contexto
        DatosDePrueba datos = ContratoBase.datosHabituales();
        List<String> escritoAMano = leerRecurso("/META-INF/diccionarios/es.txt");

        // Acción
        FicherosDePrueba.escribir(datos, carpeta);

        // Resultado esperado
        assertEquals(escritoAMano, Files.readAllLines(carpeta.resolve("es.txt"), StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Contexto: los datos habituales, con ES y EN | Acción: generar los ficheros | Resultado esperado: se llaman es.txt y en.txt, en minúsculas")
    void losFicherosSeLlamanComoElIdiomaEnMinusculas() throws IOException {
        // Contexto
        DatosDePrueba datos = ContratoBase.datosHabituales();

        // Acción
        FicherosDePrueba.escribir(datos, carpeta);

        // Resultado esperado
        // Se listan los nombres reales: en un disco que no distingue mayúsculas (el de macOS),
        // resolve("es.txt") abriría también ES.txt y la comprobación anterior no lo vería.
        try (Stream<Path> ficheros = Files.list(carpeta)) {
            assertEquals(Set.of("es.txt", "en.txt"),
                    ficheros.map(fichero -> fichero.getFileName().toString()).collect(Collectors.toSet()));
        }
    }

    @Test
    @DisplayName("Contexto: un significado con paréntesis, reservados por el formato | Acción: generar los ficheros | Resultado esperado: IllegalArgumentException")
    void losCaracteresReservadosEstanProhibidos() {
        // Contexto
        DatosDePrueba datos = new DatosDePrueba().conPalabra("ES", "melón", significado("Fruto (del melonar)"));

        // Acción y resultado esperado: van juntos porque el resultado es la propia excepción
        assertThrows(IllegalArgumentException.class, () -> FicherosDePrueba.escribir(datos, carpeta));
    }

    private static List<String> leerRecurso(String ruta) throws IOException {
        try (InputStream entrada = FicherosDePruebaTest.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new AssertionError("No está en el classpath de pruebas: " + ruta);
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        }
    }

}
