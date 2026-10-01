package com.curso.diccionario.controlador.rest.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un diccionario disponible en el servidor")
public record DiccionarioDTO(
        @Schema(description = "Código del idioma", example = "ES", requiredMode = Schema.RequiredMode.REQUIRED)
        String idioma) {
}
