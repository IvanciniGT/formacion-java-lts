package com.curso.diccionario.impl.ficheros;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.PalabraEncontrada;

import lombok.NonNull;
import lombok.Getter;
import java.util.Map;
import java.util.List;

public class DiccionarioDesdeFichero implements Diccionario {
    @Getter
    private final String idioma;
    private final Map<String, List<Significado>> palabrasYSignificados;
    // Un diccionario tendrá todas sus palabras + significados.

    public DiccionarioDesdeFichero(String idioma, Map<String, List<Significado>> palabrasYSignificados) {
        this.idioma = idioma;
        this.palabrasYSignificados = palabrasYSignificados;
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

    @Override
    public boolean existeLaPalabra(@NonNull String palabra) throws Exception { 
        return palabrasYSignificados.containsKey(palabra);
    }

    @Override
    public ResultadoDeBusquedaDePalabra buscarPalabra(@NonNull String palabra){
        List<Significado> significados = palabrasYSignificados.get(palabra);
        if(significados == null) {
            return new PalabraNoEncontrada();
        }
        return new PalabraEncontrada(significados);
    }


}
