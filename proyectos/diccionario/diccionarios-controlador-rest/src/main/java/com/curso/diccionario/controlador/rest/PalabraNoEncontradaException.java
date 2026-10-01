package com.curso.diccionario.controlador.rest;

public class PalabraNoEncontradaException extends RuntimeException {

    public PalabraNoEncontradaException(String idioma, String palabra) {
        super("La palabra " + palabra + " no existe en el idioma " + idioma);
    }
}
