package com.curso.diccionario.api;

import lombok.NonNull;

public record ErrorEnLaBusquedaDePalabra(@NonNull Exception error) implements ResultadoDeBusquedaDePalabra {}
