package com.curso.diccionario.controlador.rest.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cuerpo de todas las respuestas de error")
public record ErrorDTO(
        @Schema(description = "Motivo del error", requiredMode = Schema.RequiredMode.REQUIRED)
        CodigoDeError codigo,
        @Schema(description = "Explicación para personas; no está pensada para que la procese un programa",
                example = "No hay diccionario para el idioma XX", requiredMode = Schema.RequiredMode.REQUIRED)
        String mensaje) {
}
