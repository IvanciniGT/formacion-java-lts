package com.curso.diccionario.impl.ficheros;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;

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

    @Override
    public boolean existe(@NonNull String palabra) {
        return palabrasYSignificados.containsKey(palabra);
    }

    @Override
    public Optional<List<Significado>> getSignificados(@NonNull String palabra) {
        //return existe(palabra) ? Optional.of(palabrasYSignificados.get(palabra)) : Optional.empty();
        return Optional.ofNullable(palabrasYSignificados.get(palabra));
        // Esto hace el if por nosotros.
        // Si le pasamos nulo, devuelve Optional.empty()
        // Si le pasamos un valor, devuelve Optional.of(valor)
    }

}
