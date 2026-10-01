package com.curso.diccionario.impl.servicioweb;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.controlador.rest.api.CodigoDeError;
import com.curso.diccionario.controlador.rest.api.PalabraDTO;

import lombok.Getter;
import lombok.NonNull;

public class DiccionarioDesdeServicioWeb implements Diccionario {

    @Getter
    private final String idioma;
    private final ClienteDelServidorDeDiccionarios cliente;

    DiccionarioDesdeServicioWeb(String idioma, ClienteDelServidorDeDiccionarios cliente) {
        this.idioma = idioma;
        this.cliente = cliente;
    }

    // Se eliminará
    @Override
    public boolean existe(@NonNull String palabra) {
        try {
            return existeLaPalabra(palabra);
        } catch (Exception e) {
            return false;
        }
    }

    // Se eliminará
    @Override
    public Optional<List<Significado>> getSignificados(@NonNull String palabra) {
        ResultadoDeBusquedaDePalabra resultado = buscarPalabra(palabra);
        if (resultado instanceof PalabraEncontrada encontrada) {
            return Optional.of(encontrada.significados());
        }
        return Optional.empty();
    }

    // El API REST no tiene una operación solo para comprobar: es la misma petición que buscar.
    @Override
    public boolean existeLaPalabra(@NonNull String palabra) throws Exception {
        ResultadoDeBusquedaDePalabra resultado = buscarPalabra(palabra);
        if (resultado instanceof ErrorEnLaBusquedaDePalabra error) {
            throw error.error();
        }
        return resultado instanceof PalabraEncontrada;
    }

    @Override
    public ResultadoDeBusquedaDePalabra buscarPalabra(@NonNull String palabra) {
        try {
            HttpResponse<String> respuesta = cliente.get(cliente.rutaDePalabra(idioma, palabra));
            if (respuesta.statusCode() == 200) {
                PalabraDTO palabraDTO = cliente.leer(respuesta, PalabraDTO.class);
                return new PalabraEncontrada(palabraDTO.significados().stream()
                        // <Significado>: sin él, toList() da List<SignificadoDesdeServicioWeb>.
                        .<Significado>map(dto -> new SignificadoDesdeServicioWeb(dto.texto(), dto.ejemplos()))
                        .toList());
            }
            // Un 404 por idioma aquí no es "palabra no encontrada": el diccionario existía al crearlo.
            if (respuesta.statusCode() == 404
                    && cliente.codigoDeError(respuesta).filter(CodigoDeError.PALABRA_NO_ENCONTRADA::equals).isPresent()) {
                return new PalabraNoEncontrada();
            }
            return new ErrorEnLaBusquedaDePalabra(cliente.respuestaInesperada(respuesta));
        } catch (ErrorDelServidorDeDiccionariosException e) {
            return new ErrorEnLaBusquedaDePalabra(e);
        }
    }

}
