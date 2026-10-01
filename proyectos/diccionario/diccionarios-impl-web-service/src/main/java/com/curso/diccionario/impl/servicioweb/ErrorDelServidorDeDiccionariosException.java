package com.curso.diccionario.impl.servicioweb;

public class ErrorDelServidorDeDiccionariosException extends Exception {

    public ErrorDelServidorDeDiccionariosException(String mensaje) {
        super(mensaje);
    }

    public ErrorDelServidorDeDiccionariosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
