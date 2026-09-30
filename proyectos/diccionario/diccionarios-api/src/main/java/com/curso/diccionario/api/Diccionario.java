package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface Diccionario {

    String getIdioma();

    boolean existe(@NonNull String palabra); // Puede generar error

    // Vacío si la palabra no existe; si existe, al menos un significado.
    Optional<List<Significado>> getSignificados(@NonNull String palabra); // Puede generar error

}
