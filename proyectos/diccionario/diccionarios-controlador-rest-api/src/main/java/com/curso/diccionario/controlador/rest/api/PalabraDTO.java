package com.curso.diccionario.controlador.rest.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una palabra encontrada en un diccionario, con sus significados")
public record PalabraDTO(
        @Schema(description = "Código del idioma", example = "ES", requiredMode = Schema.RequiredMode.REQUIRED)
        String idioma,
        @Schema(description = "La palabra buscada", example = "melón", requiredMode = Schema.RequiredMode.REQUIRED)
        String palabra,
        @ArraySchema(arraySchema = @Schema(description = "Significados de la palabra",
                requiredMode = Schema.RequiredMode.REQUIRED),
                minItems = 1)
        List<SignificadoDTO> significados) {
}
