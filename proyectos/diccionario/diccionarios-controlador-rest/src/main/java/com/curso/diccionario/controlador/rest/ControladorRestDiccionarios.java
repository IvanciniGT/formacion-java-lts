package com.curso.diccionario.controlador.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.controlador.rest.api.ApiRestDiccionarios;
import com.curso.diccionario.controlador.rest.api.DiccionarioDTO;
import com.curso.diccionario.controlador.rest.api.PalabraDTO;
import com.curso.diccionario.controlador.rest.api.SignificadoDTO;

// Las URLs, los parámetros y la documentación vienen del interfaz; aquí solo hay traducción
// entre HTTP y el API Java. Los casos de error se lanzan como excepción y los convierte
// en respuesta GestorDeErroresRest, para no mezclar códigos HTTP con la lógica.
@RestController
public class ControladorRestDiccionarios implements ApiRestDiccionarios {

    private final SuministradorDeDiccionarios suministrador;

    // Solo conoce el API: la implementación (ficheros, BBDD...) la elige quien monta la aplicación.
    public ControladorRestDiccionarios(SuministradorDeDiccionarios suministrador) {
        this.suministrador = suministrador;
    }

    @Override
    public List<String> listarIdiomas() {
        return suministrador.getIdiomas();
    }

    @Override
    public DiccionarioDTO obtenerDiccionario(String idioma) {
        boolean disponible;
        try {
            disponible = suministrador.tienesDiccionarioDeIdioma(idioma);
        } catch (Exception e) {
            throw new ErrorAlConsultarDiccionarioException("Error al comprobar el diccionario de " + idioma, e);
        }
        if (!disponible) {
            throw new IdiomaNoDisponibleException(idioma);
        }
        return new DiccionarioDTO(idioma);
    }

    @Override
    public PalabraDTO buscarPalabra(String idioma, String palabra) {
        ResultadoDeBusquedaDePalabra resultado = diccionarioDe(idioma).buscarPalabra(palabra);

        // Java 17 no tiene switch con patrones: con instanceof el compilador no avisa si
        // aparece un caso nuevo en la interfaz sellada, de ahí la excepción del final.
        if (resultado instanceof PalabraEncontrada encontrada) {
            return new PalabraDTO(idioma, palabra, encontrada.significados().stream().map(this::aDTO).toList());
        }
        if (resultado instanceof PalabraNoEncontrada) {
            throw new PalabraNoEncontradaException(idioma, palabra);
        }
        if (resultado instanceof ErrorEnLaBusquedaDePalabra error) {
            throw new ErrorAlConsultarDiccionarioException("Error al buscar " + palabra + " en " + idioma, error.error());
        }
        throw new IllegalStateException("Resultado de búsqueda no previsto: " + resultado);
    }

    private Diccionario diccionarioDe(String idioma) {
        Optional<Diccionario> diccionario;
        try {
            diccionario = suministrador.getDiccionarioBuena(idioma);
        } catch (Exception e) {
            throw new ErrorAlConsultarDiccionarioException("Error al cargar el diccionario de " + idioma, e);
        }
        return diccionario.orElseThrow(() -> new IdiomaNoDisponibleException(idioma));
    }

    private SignificadoDTO aDTO(Significado significado) {
        // El contrato REST promete lista vacía, nunca null; el API Java no lo promete.
        List<String> ejemplos = significado.getEjemplos() == null ? List.of() : significado.getEjemplos();
        return new SignificadoDTO(significado.getTexto(), ejemplos);
    }
}
