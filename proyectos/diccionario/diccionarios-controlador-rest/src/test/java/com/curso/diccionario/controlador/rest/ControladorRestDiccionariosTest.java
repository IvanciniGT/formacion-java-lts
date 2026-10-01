package com.curso.diccionario.controlador.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

// Por HTTP simulado y no llamando a los métodos: lo que se prueba es lo que ve un cliente
// (URLs heredadas del interfaz, status, JSON), y eso solo existe con Spring MVC de por medio.
@WebMvcTest(ControladorRestDiccionarios.class)
class ControladorRestDiccionariosTest {

    // @WebMvcTest busca hacia arriba una configuración de Spring Boot; esta librería no tiene.
    @SpringBootApplication
    static class Configuracion {}

    record SignificadoDePrueba(String getTexto, List<String> getEjemplos) implements Significado {}

    @Autowired
    MockMvc mvc;

    @MockitoBean
    SuministradorDeDiccionarios suministrador;

    @Test
    void listaLosIdiomas() throws Exception {
        when(suministrador.getIdiomas()).thenReturn(List.of("ES", "EN"));

        mvc.perform(get("/api/v1/diccionarios"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"ES\",\"EN\"]", true));
    }

    @Test
    void confirmaUnIdiomaDisponible() throws Exception {
        when(suministrador.tienesDiccionarioDeIdioma("ES")).thenReturn(true);

        mvc.perform(get("/api/v1/diccionarios/ES"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"idioma\":\"ES\"}", true));
    }

    @Test
    void unIdiomaSinDiccionarioDa404() throws Exception {
        when(suministrador.tienesDiccionarioDeIdioma("XX")).thenReturn(false);

        mvc.perform(get("/api/v1/diccionarios/XX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("IDIOMA_NO_DISPONIBLE"));
    }

    @Test
    void devuelveLosSignificadosDeUnaPalabra() throws Exception {
        conDiccionarioQueResponde(new PalabraEncontrada(List.of(
                new SignificadoDePrueba("Fruto grande.", List.of("Me gusta el melón")),
                new SignificadoDePrueba("Persona con pocas luces.", null))));

        // Con tilde: la palabra viaja codificada en la URL y tiene que llegar decodificada.
        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"idioma":"ES","palabra":"melón","significados":[
                          {"texto":"Fruto grande.","ejemplos":["Me gusta el melón"]},
                          {"texto":"Persona con pocas luces.","ejemplos":[]}]}""", true));
    }

    @Test
    void buscarEnUnIdiomaSinDiccionarioDa404DeIdioma() throws Exception {
        when(suministrador.getDiccionarioBuena("XX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/diccionarios/XX/palabras/melón"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("IDIOMA_NO_DISPONIBLE"));
    }

    @Test
    void unaPalabraQueNoEstaDa404DePalabra() throws Exception {
        conDiccionarioQueResponde(new PalabraNoEncontrada());

        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PALABRA_NO_ENCONTRADA"));
    }

    @Test
    void unErrorEnLaBusquedaDa500SinDetallesInternos() throws Exception {
        conDiccionarioQueResponde(new ErrorEnLaBusquedaDePalabra(new IOException("/ruta/secreta/es.txt")));

        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(content().string(not(containsString("secreta"))));
    }

    @Test
    void unaExcepcionAlCargarElDiccionarioDa500() throws Exception {
        when(suministrador.getDiccionarioBuena("ES")).thenThrow(new IOException("disco roto"));

        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"));
    }

    private void conDiccionarioQueResponde(ResultadoDeBusquedaDePalabra resultado) throws Exception {
        Diccionario diccionario = mock(Diccionario.class);
        when(diccionario.buscarPalabra("melón")).thenReturn(resultado);
        when(suministrador.getDiccionarioBuena("ES")).thenReturn(Optional.of(diccionario));
    }
}
