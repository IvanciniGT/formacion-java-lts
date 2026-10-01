package com.curso.diccionario.impl.bbdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.curso.diccionario.api.Significado;

@SpringBootTest(classes = { AplicacionDePruebas.class, CargadorDeDiccionariosTest.Ficheros.class })
class CargadorDeDiccionariosTest {

    // Ficheros que cada prueba cambia a su gusto. Al ser un bean de la aplicación,
    // la autoconfiguración no crea la fuente del classpath (ConditionalOnMissingBean).
    static class FuenteEnMemoria implements FuenteDeDiccionarios {
        final Map<String, String> ficheros = new ConcurrentHashMap<>();

        @Override
        public List<String> idiomas() {
            return List.copyOf(new TreeMap<>(ficheros).keySet());
        }

        @Override
        public byte[] contenido(String idioma) throws IOException {
            return ficheros.get(idioma).getBytes(StandardCharsets.UTF_8);
        }
    }

    @Configuration
    static class Ficheros {
        @Bean
        FuenteEnMemoria fuente() {
            return new FuenteEnMemoria();
        }
    }

    @Autowired
    FuenteEnMemoria fuente;

    @Autowired
    CargadorDeDiccionarios cargador;

    @Autowired
    AlmacenDeDiccionarios almacen;

    @BeforeEach
    void sinDiccionarios() throws IOException {
        // Con la fuente vacía, el cargador borra todo lo que hubiera de la prueba anterior.
        fuente.ficheros.clear();
        cargador.cargar();
    }

    private List<String> textos(String idioma, String palabra) {
        return almacen.significados(idioma, palabra).orElseThrow().stream().map(Significado::getTexto).toList();
    }

    @Test
    @DisplayName("Contexto: un fichero de ES que no está en BBDD | Acción: cargar | Resultado esperado: se carga, con sus significados en orden")
    void cargaUnIdiomaNuevo() throws IOException {
        fuente.ficheros.put("ES", "melón=Fruto.(Rico)|Persona con pocas luces.\npera=Fruto del peral.");

        List<String> recargados = cargador.cargar();

        assertEquals(List.of("ES"), recargados);
        assertEquals(List.of("Fruto.", "Persona con pocas luces."), textos("ES", "melón"));
        assertTrue(almacen.existe("ES", "pera"));
    }

    @Test
    @DisplayName("Contexto: ES ya cargado y el fichero no ha cambiado | Acción: cargar otra vez | Resultado esperado: no se recarga nada")
    void conLaMismaHuellaNoRecarga() throws IOException {
        fuente.ficheros.put("ES", "melón=Fruto.");
        cargador.cargar();

        List<String> recargados = cargador.cargar();

        assertEquals(List.of(), recargados);
        assertTrue(almacen.existe("ES", "melón"));
    }

    @Test
    @DisplayName("Contexto: ES cargado y llega una versión nueva del fichero | Acción: cargar | Resultado esperado: quedan solo las palabras de la versión nueva")
    void unaVersionNuevaSustituyeALaAnterior() throws IOException {
        fuente.ficheros.put("ES", "melón=Fruto.\npera=Fruto del peral.");
        cargador.cargar();
        fuente.ficheros.put("ES", "melón=Fruto grande.\nsandía=Fruto enorme.");

        List<String> recargados = cargador.cargar();

        assertEquals(List.of("ES"), recargados);
        assertEquals(List.of("Fruto grande."), textos("ES", "melón"));
        assertTrue(almacen.existe("ES", "sandía"));
        assertFalse(almacen.existe("ES", "pera"), "La palabra que ya no está en el fichero debe desaparecer");
    }

    @Test
    @DisplayName("Contexto: ES y EN cargados y cambia solo EN | Acción: cargar | Resultado esperado: solo se recarga EN")
    void soloSeRecargaElIdiomaQueCambia() throws IOException {
        fuente.ficheros.put("ES", "melón=Fruto.");
        fuente.ficheros.put("EN", "melon=Fruit.");
        cargador.cargar();
        fuente.ficheros.put("EN", "melon=A fruit.");

        assertEquals(List.of("EN"), cargador.cargar());
    }

    @Test
    @DisplayName("Contexto: ES y EN cargados y desaparece el fichero de EN | Acción: cargar | Resultado esperado: EN deja de existir")
    void unIdiomaSinFicheroSeBorra() throws IOException {
        fuente.ficheros.put("ES", "melón=Fruto.");
        fuente.ficheros.put("EN", "melon=Fruit.");
        cargador.cargar();
        fuente.ficheros.remove("EN");

        cargador.cargar();

        assertEquals(List.of("ES"), almacen.idiomas());
        assertFalse(almacen.existe("EN", "melon"));
    }
}
