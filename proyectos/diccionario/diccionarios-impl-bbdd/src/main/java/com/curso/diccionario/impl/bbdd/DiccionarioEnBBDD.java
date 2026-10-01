package com.curso.diccionario.impl.bbdd;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;

// No guarda palabras: cada pregunta va a la BBDD, que es quien tiene los índices.
class DiccionarioEnBBDD implements Diccionario {

    private final String idioma;
    private final AlmacenDeDiccionarios almacen;

    DiccionarioEnBBDD(String idioma, AlmacenDeDiccionarios almacen) {
        this.idioma = idioma;
        this.almacen = almacen;
    }

    @Override
    public String getIdioma() {
        return idioma;
    }

    // Se eliminará
    @Override
    public boolean existe(String palabra) {
        Objects.requireNonNull(palabra, "palabra");
        try {
            return existeLaPalabra(palabra);
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean existeLaPalabra(String palabra) {
        return almacen.existe(idioma, Objects.requireNonNull(palabra, "palabra"));
    }

    // Se eliminará
    @Override
    public Optional<List<Significado>> getSignificados(String palabra) {
        ResultadoDeBusquedaDePalabra resultado = buscarPalabra(palabra);
        if (resultado instanceof PalabraEncontrada encontrada) {
            return Optional.of(encontrada.significados());
        }
        return Optional.empty();
    }

    @Override
    public ResultadoDeBusquedaDePalabra buscarPalabra(String palabra) {
        Objects.requireNonNull(palabra, "palabra");
        try {
            return almacen.significados(idioma, palabra)
                    .<ResultadoDeBusquedaDePalabra>map(PalabraEncontrada::new)
                    .orElseGet(PalabraNoEncontrada::new);
        } catch (RuntimeException e) {
            // Las de Spring Data (BBDD caída, timeout...) no son comprobadas: sin esto escaparían.
            return new ErrorEnLaBusquedaDePalabra(e);
        }
    }
}
