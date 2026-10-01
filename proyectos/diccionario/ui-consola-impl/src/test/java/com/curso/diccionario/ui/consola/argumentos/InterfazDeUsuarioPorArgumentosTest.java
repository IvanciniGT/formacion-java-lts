package com.curso.diccionario.ui.consola.argumentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Significado;

class InterfazDeUsuarioPorArgumentosTest {

    private final ByteArrayOutputStream escrito = new ByteArrayOutputStream();

    private InterfazDeUsuarioPorArgumentos conArgumentos(String... argumentos) {
        return new InterfazDeUsuarioPorArgumentos(List.of(argumentos), new PrintStream(escrito, true, StandardCharsets.UTF_8));
    }

    private String salida() {
        return escrito.toString(StandardCharsets.UTF_8);
    }

    record SignificadoDePrueba(String getTexto, List<String> getEjemplos) implements Significado {}

    @Test
    @DisplayName("Contexto: argumentos ES melón | Acción: obtener idioma y palabra | Resultado esperado: ES y melón")
    void leeIdiomaYPalabraDeLosArgumentos() {
        InterfazDeUsuarioPorArgumentos ui = conArgumentos("ES", "melón");

        assertEquals(Optional.of("ES"), ui.obtenerIdiomaDelUsuario());
        assertEquals(Optional.of("melón"), ui.obtenerPalabraDelUsuario());
    }

    @Test
    @DisplayName("Contexto: solo el argumento ES | Acción: obtener la palabra | Resultado esperado: vacío")
    void sinSegundoArgumentoNoHayPalabra() {
        assertEquals(Optional.empty(), conArgumentos("ES").obtenerPalabraDelUsuario());
    }

    @Test
    @DisplayName("Contexto: argumentos ES y un espacio en blanco | Acción: obtener la palabra | Resultado esperado: vacío")
    void unArgumentoEnBlancoEsComoNoDarlo() {
        assertEquals(Optional.empty(), conArgumentos("ES", "  ").obtenerPalabraDelUsuario());
    }

    @Test
    @DisplayName("Contexto: melón con un significado con 2 ejemplos y otro sin | Acción: mostrarlos | Resultado esperado: el formato de siempre, en orden")
    void muestraLosSignificadosConSusEjemplos() {
        conArgumentos().mostrarSignificadosDePalabra("ES", "melón", List.of(
                new SignificadoDePrueba("Persona con pocas luces.", List.of("Eres un melón", "No seas melón")),
                new SignificadoDePrueba("Fruto.", List.of())));

        assertEquals("""
                La palabra melón existe en el idioma ES y tiene los siguientes significados:
                - Persona con pocas luces.
                    Ej: Eres un melón
                    Ej: No seas melón
                - Fruto.
                """, salida().replace(System.lineSeparator(), "\n"));
    }

    @Test
    @DisplayName("Contexto: un error con mensaje | Acción: mostrarlo | Resultado esperado: el mensaje y el aviso de reintentar")
    void elErrorGenericoMuestraElMensaje() {
        conArgumentos().mostrarErrorGenerico(new IllegalStateException("se fue la luz"));

        assertTrue(salida().contains("Ocurrió un error al procesar la petición: se fue la luz"), salida());
        assertTrue(salida().contains("Inténtelo de nuevo más tarde."), salida());
    }
}
