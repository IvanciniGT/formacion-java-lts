package com.curso.diccionario.impl.ficheros;

import static com.curso.diccionario.api.contrato.ContratoBase.MELON_FRUTO;
import static com.curso.diccionario.api.contrato.ContratoBase.MELON_PERSONA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;

/**
 * Las pruebas de contrato siempre dan una carpeta en disco. Esta prueba el otro camino: el que usa
 * el ServiceLoader cuando no hay variable de entorno, leyendo META-INF/diccionarios/ del classpath.
 */
class SuministradorDesdeClasspathTest {

    @Test
    @DisplayName("Contexto: sin DICCIONARIOS_CARPETA y con META-INF/diccionarios/es.txt en el classpath | Acción: pedir al provider los significados de melón en ES | Resultado esperado: los 2 de es.txt, en orden")
    void sinVariableDeEntornoLeeLosDiccionariosDelClasspath() {
        // Contexto
        assumeTrue(System.getenv(SuministradorDeDiccionariosDesdeFicheros.VARIABLE_DE_ENTORNO_CARPETA) == null,
                "Con la variable definida el provider no mira el classpath: esta prueba no aplica");
        SuministradorDeDiccionarios suministrador = SuministradorDeDiccionariosDesdeFicheros.provider();

        // Acción
        Optional<List<Significado>> significados = suministrador.getDiccionario("ES")
                .flatMap(diccionario -> diccionario.getSignificados("melón"));

        // Resultado esperado
        assertEquals(List.of(MELON_FRUTO, MELON_PERSONA),
                significados.orElseThrow().stream().map(SignificadoDePrueba::de).toList());
    }

    @Test
    @DisplayName("Contexto: sin DICCIONARIOS_CARPETA y con un índice META-INF/diccionarios/idiomas que anuncia ES | Acción: pedir los idiomas al provider | Resultado esperado: ES, sacado del índice")
    void sinVariableDeEntornoLosIdiomasSalenDelIndice() throws Exception {
        // Contexto
        assumeTrue(System.getenv(SuministradorDeDiccionariosDesdeFicheros.VARIABLE_DE_ENTORNO_CARPETA) == null,
                "Con la variable definida el provider no mira el classpath: esta prueba no aplica");
        SuministradorDeDiccionarios suministrador = SuministradorDeDiccionariosDesdeFicheros.provider();

        // Acción
        List<String> idiomas = suministrador.getIdiomasDisponibles();

        // Resultado esperado
        assertEquals(List.of("ES"), idiomas);
    }

}
