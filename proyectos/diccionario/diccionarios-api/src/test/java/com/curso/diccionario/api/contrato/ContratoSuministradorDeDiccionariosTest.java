package com.curso.diccionario.api.contrato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

/**
 * Caja negra: lo que cualquier SuministradorDeDiccionarios tiene que cumplir,
 * trabaje contra ficheros, BBDD o lo que sea.
 */
public abstract class ContratoSuministradorDeDiccionariosTest extends ContratoBase {

    // ---------------------------------------------------------------- getIdiomas

    @Test
    @DisplayName("Contexto: suministrador con diccionarios de ES y EN | Acción: pedir sus idiomas | Resultado esperado: ES y EN, sin repetir")
    void getIdiomasDevuelveLosIdiomasDeSusDiccionarios() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        List<String> idiomas = suministrador.getIdiomas();

        // Resultado esperado
        // El contrato no fija el orden, así que se compara como conjunto; pero sí que no haya repetidos.
        assertEquals(Set.of("ES", "EN"), new HashSet<>(idiomas));
        assertEquals(2, idiomas.size(), "No debe repetir idiomas: " + idiomas);
    }

    @Test
    @DisplayName("Contexto: suministrador sin diccionarios | Acción: pedir sus idiomas | Resultado esperado: lista vacía, no null")
    void getIdiomasSinDiccionariosDevuelveListaVacia() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(new DatosDePrueba());

        // Acción
        List<String> idiomas = suministrador.getIdiomas();

        // Resultado esperado
        assertNotNull(idiomas);
        assertTrue(idiomas.isEmpty(), "Esperaba ningún idioma y hay: " + idiomas);
    }

    @Test
    @DisplayName("Contexto: suministrador con un diccionario de ES sin palabras | Acción: pedir sus idiomas | Resultado esperado: ES aparece igualmente")
    void unDiccionarioVacioTambienEsUnIdioma() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(new DatosDePrueba().conDiccionario("ES"));

        // Acción
        List<String> idiomas = suministrador.getIdiomas();

        // Resultado esperado
        assertEquals(List.of("ES"), idiomas);
    }

    @Test
    @DisplayName("Contexto: suministrador con ES y EN | Acción: pedir los idiomas con getIdiomasDisponibles | Resultado esperado: los mismos que getIdiomas")
    void getIdiomasDisponiblesDiceLoMismoQueGetIdiomas() throws Exception {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        List<String> disponibles = suministrador.getIdiomasDisponibles();

        // Resultado esperado
        // El nuevo solo cambia cómo se informa de un error: sin errores, la respuesta es la misma.
        assertEquals(new HashSet<>(suministrador.getIdiomas()), new HashSet<>(disponibles));
        assertEquals(2, disponibles.size(), "No debe repetir idiomas: " + disponibles);
    }

    // ------------------------------------------------------- tienesDiccionarioDe

    @Test
    @DisplayName("Contexto: suministrador con diccionario de ES | Acción: preguntar si tiene ES | Resultado esperado: true")
    void tienesDiccionarioDeUnIdiomaQueTiene() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        boolean loTiene = suministrador.tienesDiccionarioDe("ES");

        // Resultado esperado
        assertTrue(loTiene);
    }

    @Test
    @DisplayName("Contexto: suministrador sin diccionario de ELF | Acción: preguntar si tiene ELF | Resultado esperado: false")
    void tienesDiccionarioDeUnIdiomaQueNoTiene() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        boolean loTiene = suministrador.tienesDiccionarioDe("ELF");

        // Resultado esperado
        assertFalse(loTiene);
    }

    @Test
    @DisplayName("Contexto: suministrador con diccionarios | Acción: preguntar si tiene el idioma null | Resultado esperado: NullPointerException")
    void tienesDiccionarioDeNullLanzaNullPointerException() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción y resultado esperado: van juntos porque el resultado es la propia excepción
        assertThrows(NullPointerException.class, () -> suministrador.tienesDiccionarioDe(null));
    }

    // ------------------------------------------------------------ getDiccionario

    @Test
    @DisplayName("Contexto: suministrador con diccionario de ES | Acción: pedir el diccionario de ES | Resultado esperado: lo entrega y es de ES")
    void getDiccionarioDeUnIdiomaQueTiene() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        Optional<Diccionario> diccionario = suministrador.getDiccionario("ES");

        // Resultado esperado
        assertTrue(diccionario.isPresent());
        assertEquals("ES", diccionario.get().getIdioma());
    }

    @Test
    @DisplayName("Contexto: suministrador sin diccionario de ELF | Acción: pedir el diccionario de ELF | Resultado esperado: Optional vacío, no null")
    void getDiccionarioDeUnIdiomaQueNoTiene() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        Optional<Diccionario> diccionario = suministrador.getDiccionario("ELF");

        // Resultado esperado
        assertNotNull(diccionario, "Nunca null: la caja siempre se devuelve");
        assertTrue(diccionario.isEmpty());
    }

    @Test
    @DisplayName("Contexto: suministrador con diccionarios | Acción: pedir el diccionario del idioma null | Resultado esperado: NullPointerException")
    void getDiccionarioDeNullLanzaNullPointerException() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción y resultado esperado: van juntos porque el resultado es la propia excepción
        assertThrows(NullPointerException.class, () -> suministrador.getDiccionario(null));
    }

    @Test
    @DisplayName("Contexto: suministrador con ES y EN | Acción: pedir el diccionario de cada idioma | Resultado esperado: cada uno es de su idioma")
    void cadaDiccionarioEsDeSuIdioma() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        Diccionario espanol = suministrador.getDiccionario("ES").orElseThrow();
        Diccionario ingles = suministrador.getDiccionario("EN").orElseThrow();

        // Resultado esperado
        assertEquals("ES", espanol.getIdioma());
        assertEquals("EN", ingles.getIdioma());
    }

    @Test
    @DisplayName("Contexto: suministrador con ES al que ya se le pidió ES una vez | Acción: volver a pedir ES | Resultado esperado: lo entrega y funciona")
    void getDiccionarioRepetidoSigueFuncionando() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());
        suministrador.getDiccionario("ES");

        // Acción
        Optional<Diccionario> segundaVez = suministrador.getDiccionario("ES");

        // Resultado esperado
        assertTrue(segundaVez.isPresent());
        assertTrue(segundaVez.get().existe("melón"));
    }

    // ------------------------------------------------------- coherencia interna

    @Test
    @DisplayName("Contexto: suministrador con ES y EN | Acción: preguntar por ES, EN, ELF y '' con tienesDiccionarioDe y con getDiccionario | Resultado esperado: ambos dicen lo mismo")
    void tienesDiccionarioDeYGetDiccionarioSonCoherentes() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        Map<String, Boolean> segunTienes = new LinkedHashMap<>();
        Map<String, Boolean> segunGet = new LinkedHashMap<>();
        for (String idioma : List.of("ES", "EN", "ELF", "")) {
            segunTienes.put(idioma, suministrador.tienesDiccionarioDe(idioma));
            segunGet.put(idioma, suministrador.getDiccionario(idioma).isPresent());
        }

        // Resultado esperado
        assertEquals(segunTienes, segunGet);
    }

    @Test
    @DisplayName("Contexto: suministrador con ES y EN | Acción: preguntar por cada idioma que anuncia getIdiomas | Resultado esperado: tiene diccionario de todos")
    void getIdiomasYTienesDiccionarioDeSonCoherentes() {
        // Contexto
        SuministradorDeDiccionarios suministrador = crearSuministradorCon(datosHabituales());

        // Acción
        List<String> sinDiccionario = suministrador.getIdiomas().stream()
                .filter(idioma -> !suministrador.tienesDiccionarioDe(idioma))
                .toList();

        // Resultado esperado
        assertTrue(sinDiccionario.isEmpty(), "Anuncia idiomas que luego dice no tener: " + sinDiccionario);
    }

}
