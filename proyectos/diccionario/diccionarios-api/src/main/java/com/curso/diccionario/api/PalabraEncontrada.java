package com.curso.diccionario.api;

import java.util.List;

import lombok.NonNull;

public record PalabraEncontrada(@NonNull List<Significado> significados) implements ResultadoDeBusquedaDePalabra {
    public PalabraEncontrada {
        // Si no hay significados, el caso correcto es PalabraNoEncontrada.
        if (significados.isEmpty())
            throw new IllegalArgumentException("Una palabra encontrada debe tener al menos un significado");
        significados = List.copyOf(significados);
    }
}
