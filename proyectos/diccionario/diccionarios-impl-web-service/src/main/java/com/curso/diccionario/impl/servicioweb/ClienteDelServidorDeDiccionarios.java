package com.curso.diccionario.impl.servicioweb;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import com.curso.diccionario.controlador.rest.api.ApiRestDiccionarios;
import com.curso.diccionario.controlador.rest.api.CodigoDeError;
import com.curso.diccionario.controlador.rest.api.ErrorDTO;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

// Lo común a todas las peticiones: construir la URL, lanzarla y leer el JSON.
// Lo comparten el suministrador y sus diccionarios.
final class ClienteDelServidorDeDiccionarios {

    // Sin límites, un servidor colgado deja colgada también a la aplicación que nos usa.
    private static final Duration TIEMPO_MAXIMO_DE_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIEMPO_MAXIMO_DE_RESPUESTA = Duration.ofSeconds(10);

    private final String servidor;
    private final HttpClient clienteHttp;
    private final Gson gson = new Gson();

    ClienteDelServidorDeDiccionarios(String servidor) {
        // Las rutas del API ya empiezan por "/": con la barra final quedaría "//api/...".
        this.servidor = servidor.endsWith("/") ? servidor.substring(0, servidor.length() - 1) : servidor;
        this.clienteHttp = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(TIEMPO_MAXIMO_DE_CONEXION)
                .build();
    }

    // Las rutas salen de las constantes del API REST: si cambian allí, cambian aquí.
    String rutaDeDiccionarios() {
        return ApiRestDiccionarios.RUTA_DICCIONARIOS;
    }

    String rutaDeDiccionario(String idioma) {
        return ApiRestDiccionarios.RUTA_DICCIONARIOS + ApiRestDiccionarios.RUTA_DICCIONARIO
                .replace("{" + ApiRestDiccionarios.PARAMETRO_IDIOMA + "}", codificar(idioma));
    }

    String rutaDePalabra(String idioma, String palabra) {
        return ApiRestDiccionarios.RUTA_DICCIONARIOS + ApiRestDiccionarios.RUTA_PALABRA
                .replace("{" + ApiRestDiccionarios.PARAMETRO_IDIOMA + "}", codificar(idioma))
                .replace("{" + ApiRestDiccionarios.PARAMETRO_PALABRA + "}", codificar(palabra));
    }

    HttpResponse<String> get(String ruta) throws ErrorDelServidorDeDiccionariosException {
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(servidor + ruta))
                .timeout(TIEMPO_MAXIMO_DE_RESPUESTA)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            return clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ErrorDelServidorDeDiccionariosException("No se pudo conectar con " + peticion.uri(), e);
        } catch (InterruptedException e) {
            // Quien nos interrumpió tiene que poder enterarse: se restaura la marca antes de seguir.
            Thread.currentThread().interrupt();
            throw new ErrorDelServidorDeDiccionariosException("Petición interrumpida: " + peticion.uri(), e);
        }
    }

    <T> T leer(HttpResponse<String> respuesta, Type tipo) throws ErrorDelServidorDeDiccionariosException {
        try {
            return gson.fromJson(respuesta.body(), tipo);
        } catch (JsonParseException e) {
            throw new ErrorDelServidorDeDiccionariosException("Respuesta no válida de " + respuesta.uri(), e);
        }
    }

    // Un 404 no basta: puede ser el idioma, la palabra o una URL que el servidor no conoce
    // (servidor mal configurado). Solo el código del cuerpo dice cuál de ellos es.
    Optional<CodigoDeError> codigoDeError(HttpResponse<String> respuesta) {
        try {
            ErrorDTO error = gson.fromJson(respuesta.body(), ErrorDTO.class);
            return Optional.ofNullable(error).map(ErrorDTO::codigo);
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }

    ErrorDelServidorDeDiccionariosException respuestaInesperada(HttpResponse<String> respuesta) {
        return new ErrorDelServidorDeDiccionariosException(
                "El servidor respondió " + respuesta.statusCode() + " a " + respuesta.uri() + ": " + respuesta.body());
    }

    // URLEncoder es para formularios y pone el espacio como "+"; en una ruta tiene que ser %20.
    // Sin codificar, una palabra con espacio, "?", "#" o "/" rompe la URL o pide otra.
    private static String codificar(String segmento) {
        return URLEncoder.encode(segmento, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
