package com.curso.diccionario.api.contrato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;

/**
 * Caja negra: lo que cualquier Diccionario tiene que cumplir. El diccionario se obtiene
 * siempre a través del suministrador, que es la única puerta que el API ofrece.
 */
public abstract class ContratoDiccionarioTest extends ContratoBase {

    private Diccionario diccionarioDe(String idioma, DatosDePrueba datos) {
        return crearSuministradorCon(datos).getDiccionario(idioma)
                .orElseThrow(() -> new AssertionError("El suministrador no entrega el diccionario " + idioma));
    }

    private static List<SignificadoDePrueba> comparables(List<Significado> significados) {
        return significados.stream().map(SignificadoDePrueba::de).toList();
    }

    // -------------------------------------------------------------------- existe

    @Test
    @DisplayName("Contexto: diccionario de ES con la palabra melón | Acción: preguntar si existe melón | Resultado esperado: true")
    void existeUnaPalabraQueEsta() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        boolean existe = espanol.existe("melón");

        // Resultado esperado
        assertTrue(existe);
    }

    @Test
    @DisplayName("Contexto: diccionario de ES sin la palabra archilococo | Acción: preguntar si existe archilococo | Resultado esperado: false")
    void noExisteUnaPalabraQueNoEsta() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        boolean existe = espanol.existe("archilococo");

        // Resultado esperado
        assertFalse(existe);
    }

    @Test
    @DisplayName("Contexto: ES tiene melón y EN tiene melon | Acción: preguntar a cada uno por la palabra del otro | Resultado esperado: false en ambos")
    void unDiccionarioNoVeLasPalabrasDeOtroIdioma() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());
        Diccionario ingles = diccionarioDe("EN", datosHabituales());

        // Acción
        boolean melonEnEspanol = espanol.existe("melon");
        boolean melonEnIngles = ingles.existe("melón");

        // Resultado esperado
        assertFalse(melonEnEspanol);
        assertFalse(melonEnIngles);
    }

    @Test
    @DisplayName("Contexto: diccionario de ES | Acción: preguntar si existe null | Resultado esperado: NullPointerException")
    void existeNullLanzaNullPointerException() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción y resultado esperado: van juntos porque el resultado es la propia excepción
        assertThrows(NullPointerException.class, () -> espanol.existe(null));
    }

    // ----------------------------------------------------------- getSignificados

    @Test
    @DisplayName("Contexto: ES con melón y 2 significados | Acción: pedir los significados de melón | Resultado esperado: los 2, en orden y con sus ejemplos")
    void getSignificadosDeUnaPalabraQueEsta() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        Optional<List<Significado>> significados = espanol.getSignificados("melón");

        // Resultado esperado
        assertTrue(significados.isPresent());
        // En un diccionario el orden de las acepciones es información: la primera es la principal.
        assertEquals(List.of(MELON_FRUTO, MELON_PERSONA), comparables(significados.get()));
    }

    @Test
    @DisplayName("Contexto: ES con pera y un significado sin ejemplos | Acción: pedir los ejemplos de ese significado | Resultado esperado: lista vacía, no null")
    void unSignificadoSinEjemplosDevuelveListaVacia() {
        // Contexto
        Significado significadoDePera = diccionarioDe("ES", datosHabituales()).getSignificados("pera").orElseThrow().get(0);

        // Acción
        List<String> ejemplos = significadoDePera.getEjemplos();

        // Resultado esperado
        assertNotNull(ejemplos);
        assertTrue(ejemplos.isEmpty());
    }

    @Test
    @DisplayName("Contexto: ES sin la palabra archilococo | Acción: pedir los significados de archilococo | Resultado esperado: Optional vacío, no null")
    void getSignificadosDeUnaPalabraQueNoEsta() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        Optional<List<Significado>> significados = espanol.getSignificados("archilococo");

        // Resultado esperado
        assertNotNull(significados, "Nunca null: la caja siempre se devuelve");
        assertTrue(significados.isEmpty());
    }

    @Test
    @DisplayName("Contexto: EN con melon | Acción: pedir a EN los significados de melon | Resultado esperado: los de EN")
    void getSignificadosEnOtroIdioma() {
        // Contexto
        Diccionario ingles = diccionarioDe("EN", datosHabituales());

        // Acción
        Optional<List<Significado>> significados = ingles.getSignificados("melon");

        // Resultado esperado
        assertEquals(List.of(MELON_EN), comparables(significados.orElseThrow()));
    }

    @Test
    @DisplayName("Contexto: diccionario de ES | Acción: pedir los significados de null | Resultado esperado: NullPointerException")
    void getSignificadosDeNullLanzaNullPointerException() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción y resultado esperado: van juntos porque el resultado es la propia excepción
        assertThrows(NullPointerException.class, () -> espanol.getSignificados(null));
    }

    @Test
    @DisplayName("Contexto: ES con melón, cuyos significados ya se pidieron una vez | Acción: volver a pedirlos | Resultado esperado: los mismos 2, en orden")
    void getSignificadosRepetidoDevuelveLoMismo() {
        // Contexto
        // Es el camino que recorre una implementación con caché: la segunda vez no va al origen.
        Diccionario espanol = diccionarioDe("ES", datosHabituales());
        espanol.getSignificados("melón");

        // Acción
        Optional<List<Significado>> segundaVez = espanol.getSignificados("melón");

        // Resultado esperado
        assertEquals(List.of(MELON_FRUTO, MELON_PERSONA), comparables(segundaVez.orElseThrow()));
    }

    @Test
    @DisplayName("Contexto: ES sin archilococo, por la que ya se preguntó una vez | Acción: volver a preguntar | Resultado esperado: sigue sin existir")
    void getSignificadosRepetidoDeUnaPalabraQueNoEsta() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());
        espanol.getSignificados("archilococo");

        // Acción
        Optional<List<Significado>> segundaVez = espanol.getSignificados("archilococo");

        // Resultado esperado
        assertTrue(segundaVez.isEmpty());
        assertFalse(espanol.existe("archilococo"));
    }

    // ------------------------------------------------------- coherencia interna

    @Test
    @DisplayName("Contexto: diccionario de ES | Acción: preguntar por varias palabras con existe y con getSignificados | Resultado esperado: ambos dicen lo mismo")
    void existeYGetSignificadosSonCoherentes() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        Map<String, Boolean> segunExiste = new LinkedHashMap<>();
        Map<String, Boolean> segunSignificados = new LinkedHashMap<>();
        for (String palabra : List.of("melón", "pera", "archilococo", "melon", "")) {
            segunExiste.put(palabra, espanol.existe(palabra));
            segunSignificados.put(palabra, espanol.getSignificados(palabra).isPresent());
        }

        // Resultado esperado
        assertEquals(segunExiste, segunSignificados);
    }

    @Test
    @DisplayName("Contexto: ES con melón y pera | Acción: pedir sus significados | Resultado esperado: ninguna lista vacía (presente pero vacía sería ambiguo)")
    void unaPalabraQueExisteNuncaTieneListaVacia() {
        // Contexto
        Diccionario espanol = diccionarioDe("ES", datosHabituales());

        // Acción
        List<String> presentesPeroVacias = List.of("melón", "pera").stream()
                .filter(palabra -> espanol.getSignificados(palabra).orElseThrow().isEmpty())
                .toList();

        // Resultado esperado
        assertTrue(presentesPeroVacias.isEmpty(),
                "Presente pero vacía es la ambigüedad que Optional viene a quitar: " + presentesPeroVacias);
    }

}
