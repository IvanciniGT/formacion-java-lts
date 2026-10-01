package com.curso.diccionario.controlador.rest;

// Los métodos del API REST no declaran excepciones comprobadas, y el API Java sí las lanza:
// se envuelven aquí y el gestor de errores las convierte en un 500.
public class ErrorAlConsultarDiccionarioException extends RuntimeException {

    public ErrorAlConsultarDiccionarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
