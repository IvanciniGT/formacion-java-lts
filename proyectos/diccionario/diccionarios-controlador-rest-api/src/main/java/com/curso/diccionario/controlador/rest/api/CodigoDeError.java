package com.curso.diccionario.controlador.rest.api;

import io.swagger.v3.oas.annotations.media.Schema;

// El status HTTP no basta: un 404 puede ser por el idioma o por la palabra,
// y el cliente necesita distinguirlos sin interpretar el mensaje.
@Schema(description = "Motivo del error, para que el cliente decida qué hacer sin leer el mensaje")
public enum CodigoDeError {
    IDIOMA_NO_DISPONIBLE,
    PALABRA_NO_ENCONTRADA,
    ERROR_INTERNO
}
