package com.curso.diccionario.controlador.rest.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

// El contrato HTTP del servidor de diccionarios, escrito una sola vez:
// la implementación hereda los mappings y springdoc genera la especificación OpenAPI desde aquí.
// Así no hay una spec y un código que puedan dejar de coincidir (DRY).
@OpenAPIDefinition(
        info = @Info(
                title = "Servidor de diccionarios",
                version = "1.0.0",
                description = "Consulta de palabras y sus significados en los diccionarios de cada idioma."),
        // Relativa: si no, la spec generada al empaquetar llevaría el puerto aleatorio de la generación.
        servers = @Server(url = "/", description = "El mismo servidor que sirve esta especificación"))
@Tag(name = "Diccionarios", description = "Idiomas disponibles y búsqueda de palabras")
@RequestMapping(path = ApiRestDiccionarios.RUTA_DICCIONARIOS, produces = MediaType.APPLICATION_JSON_VALUE)
public interface ApiRestDiccionarios {

    // Públicas para que el cliente REST construya las mismas URLs sin copiarlas a mano.
    String RUTA_DICCIONARIOS = "/api/v1/diccionarios";
    String PARAMETRO_IDIOMA = "idioma";
    String PARAMETRO_PALABRA = "palabra";
    String RUTA_DICCIONARIO = "/{" + PARAMETRO_IDIOMA + "}";
    String RUTA_PALABRA = RUTA_DICCIONARIO + "/palabras/{" + PARAMETRO_PALABRA + "}";

    @Operation(
            operationId = "listarIdiomas",
            summary = "Idiomas con diccionario",
            description = "Códigos de los idiomas para los que el servidor tiene diccionario. Puede ser una lista vacía.")
    @ApiResponse(responseCode = "200", description = "Lista de códigos de idioma",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(type = "string", example = "ES")),
                    examples = @ExampleObject(name = "Un idioma", value = "[\"ES\"]")))
    @ApiResponse(responseCode = "500", description = "Error al consultar los diccionarios",
            content = @Content(schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Error interno", value = Ejemplos.ERROR_INTERNO)))
    @GetMapping
    List<String> listarIdiomas();

    @Operation(
            operationId = "obtenerDiccionario",
            summary = "Comprueba si hay diccionario para un idioma",
            description = "Responde 200 si el servidor tiene diccionario para el idioma y 404 si no.")
    @ApiResponse(responseCode = "200", description = "Hay diccionario para el idioma",
            content = @Content(schema = @Schema(implementation = DiccionarioDTO.class),
                    examples = @ExampleObject(name = "Español", value = "{\"idioma\":\"ES\"}")))
    @ApiResponse(responseCode = "404", description = "No hay diccionario para el idioma",
            content = @Content(schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Idioma no disponible", value = Ejemplos.IDIOMA_NO_DISPONIBLE)))
    @ApiResponse(responseCode = "500", description = "Error al consultar los diccionarios",
            content = @Content(schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Error interno", value = Ejemplos.ERROR_INTERNO)))
    @GetMapping(RUTA_DICCIONARIO)
    DiccionarioDTO obtenerDiccionario(
            @Parameter(description = "Código del idioma", example = "ES")
            // Nombre explícito: sin él, Spring depende de compilar con -parameters.
            @PathVariable(PARAMETRO_IDIOMA) String idioma);

    @Operation(
            operationId = "buscarPalabra",
            summary = "Busca una palabra en el diccionario de un idioma",
            description = """
                    Devuelve los significados de la palabra, cada uno con sus ejemplos (la lista puede venir vacía).
                    Una palabra encontrada tiene siempre al menos un significado.
                    Los dos 404 se distinguen por el campo `codigo` del error.""")
    @ApiResponse(responseCode = "200", description = "La palabra existe",
            content = @Content(schema = @Schema(implementation = PalabraDTO.class),
                    examples = @ExampleObject(name = "melón", value = Ejemplos.PALABRA_MELON)))
    @ApiResponse(responseCode = "404", description = "No hay diccionario para el idioma, o la palabra no está en él",
            content = @Content(schema = @Schema(implementation = ErrorDTO.class),
                    examples = {
                            @ExampleObject(name = "Idioma no disponible", value = Ejemplos.IDIOMA_NO_DISPONIBLE),
                            @ExampleObject(name = "Palabra no encontrada", value = Ejemplos.PALABRA_NO_ENCONTRADA)
                    }))
    @ApiResponse(responseCode = "500", description = "Error al buscar la palabra",
            content = @Content(schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Error interno", value = Ejemplos.ERROR_INTERNO)))
    @GetMapping(RUTA_PALABRA)
    PalabraDTO buscarPalabra(
            @Parameter(description = "Código del idioma", example = "ES")
            @PathVariable(PARAMETRO_IDIOMA) String idioma,
            @Parameter(description = "Palabra a buscar, tal cual aparece en el diccionario", example = "melón")
            @PathVariable(PARAMETRO_PALABRA) String palabra);

    // Las anotaciones solo aceptan constantes: los ejemplos repetidos viven aquí.
    final class Ejemplos {
        static final String ERROR_INTERNO = """
                {"codigo":"ERROR_INTERNO","mensaje":"Error al consultar el diccionario"}""";
        static final String IDIOMA_NO_DISPONIBLE = """
                {"codigo":"IDIOMA_NO_DISPONIBLE","mensaje":"No hay diccionario para el idioma XX"}""";
        static final String PALABRA_NO_ENCONTRADA = """
                {"codigo":"PALABRA_NO_ENCONTRADA","mensaje":"La palabra patata no existe en el idioma ES"}""";
        static final String PALABRA_MELON = """
                {"idioma":"ES","palabra":"melón","significados":[\
                {"texto":"Fruto grande, redondo y de pulpa jugosa y dulce.","ejemplos":["Me gusta comer melón!"]},\
                {"texto":"Persona con pocas luces.","ejemplos":["Eres un melón","No seas melón"]}]}""";

        private Ejemplos() {}
    }
}
