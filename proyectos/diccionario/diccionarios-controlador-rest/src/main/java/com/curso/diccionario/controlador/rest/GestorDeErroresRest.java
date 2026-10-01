package com.curso.diccionario.controlador.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.curso.diccionario.controlador.rest.api.CodigoDeError;
import com.curso.diccionario.controlador.rest.api.ErrorDTO;

// Limitado a este controlador: el Exception.class de abajo no debe tragarse los errores
// de otros controladores que monte la misma aplicación.
@RestControllerAdvice(assignableTypes = ControladorRestDiccionarios.class)
public class GestorDeErroresRest {

    private static final Logger log = LoggerFactory.getLogger(GestorDeErroresRest.class);

    @ExceptionHandler(IdiomaNoDisponibleException.class)
    public ResponseEntity<ErrorDTO> idiomaNoDisponible(IdiomaNoDisponibleException e) {
        return respuesta(HttpStatus.NOT_FOUND, CodigoDeError.IDIOMA_NO_DISPONIBLE, e.getMessage());
    }

    @ExceptionHandler(PalabraNoEncontradaException.class)
    public ResponseEntity<ErrorDTO> palabraNoEncontrada(PalabraNoEncontradaException e) {
        return respuesta(HttpStatus.NOT_FOUND, CodigoDeError.PALABRA_NO_ENCONTRADA, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> errorInterno(Exception e) {
        log.error("Error al atender una petición de diccionarios", e);
        // Mensaje fijo: el de la excepción puede llevar rutas o detalles internos del servidor.
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, CodigoDeError.ERROR_INTERNO, "Error al consultar el diccionario");
    }

    private static ResponseEntity<ErrorDTO> respuesta(HttpStatus estado, CodigoDeError codigo, String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorDTO(codigo, mensaje));
    }
}
