package com.curso.diccionario.controlador.rest.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una de las acepciones de una palabra")
public record SignificadoDTO(
        @Schema(description = "Texto del significado", example = "Persona con pocas luces.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String texto,
        // Lista vacía y no ausente cuando no hay ejemplos: el cliente no tiene que comprobar null.
        @ArraySchema(arraySchema = @Schema(description = "Frases de ejemplo; vacía si no hay",
                requiredMode = Schema.RequiredMode.REQUIRED),
                schema = @Schema(example = "No seas melón"))
        List<String> ejemplos) {
}
