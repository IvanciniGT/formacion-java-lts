package com.curso.diccionario.api;

// Sellada para que un switch sobre el resultado sea exhaustivo sin default:
// si mañana se añade otro caso, el compilador señala cada switch que no lo trata.
public sealed interface ResultadoDeBusquedaDePalabra
        permits PalabraEncontrada, PalabraNoEncontrada, ErrorEnLaBusquedaDePalabra {
}
