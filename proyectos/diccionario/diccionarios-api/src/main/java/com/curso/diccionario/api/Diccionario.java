package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface Diccionario {

    String getIdioma();

    @Deprecated(since = "1.1.0", forRemoval = true)
    boolean existe(@NonNull String palabra); // Puede generar error
    
    default boolean existeLaPalabra(@NonNull String palabra) throws Exception { // Puede generar error
        return existe(palabra);
    }

    // Vacío si la palabra no existe; si existe, al menos un significado.
    @Deprecated(since = "1.1.0", forRemoval = true)
    Optional<List<Significado>> getSignificados(@NonNull String palabra); // Puede generar error
    
    // Para ver como funcionan los sealed interfaces/clases, vamos a usar eso para encapsular la exception.
    // Aunque en este caso, lo natural sería devolver excepcion, y el Optional.
    default ResultadoDeBusquedaDePalabra buscarPalabra(@NonNull String palabra) {
        Optional<List<Significado>> significados = getSignificados(palabra);
        if (significados.isPresent() && !significados.get().isEmpty()) {
            return new PalabraEncontrada(significados.get());
        } else {
            return new PalabraNoEncontrada();
        }
        // Esta implementación por defecto no usa la clase ErrorEnLaBusquedaDePalabra.
        // PEro es que la funcion antigua no generaba excepciones, por eso no se usa ErrorEnLaBusquedaDePalabra aquí.
    }

}
