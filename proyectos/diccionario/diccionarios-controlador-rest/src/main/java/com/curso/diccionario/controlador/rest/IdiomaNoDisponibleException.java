package com.curso.diccionario.controlador.rest;

public class IdiomaNoDisponibleException extends RuntimeException {

    public IdiomaNoDisponibleException(String idioma) {
        super("No hay diccionario para el idioma " + idioma);
    }
}
