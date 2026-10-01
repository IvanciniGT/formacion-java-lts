package com.curso.diccionario.app.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

// Que todo encaja de verdad: el bean del suministrador, los jars de idioma en el classpath,
// el controlador importado y springdoc. Vale con ficheros y con el perfil bbdd (BBDD en memoria).
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:arranque")
class ServidorArrancaTest {

    @Value("${local.server.port}")
    int puerto;

    private HttpResponse<String> get(String ruta) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("Contexto: el servidor con los jars diccionario-es y diccionario-en | Acción: pedir los idiomas | Resultado esperado: EN y ES")
    void sirveLosIdiomasDeLosJars() throws Exception {
        HttpResponse<String> respuesta = get("/api/v1/diccionarios");

        assertEquals(200, respuesta.statusCode());
        assertEquals("[\"EN\",\"ES\"]", respuesta.body());
    }

    @Test
    @DisplayName("Contexto: el servidor arrancado | Acción: buscar melón en ES | Resultado esperado: 200 con sus significados")
    void encuentraUnaPalabra() throws Exception {
        HttpResponse<String> respuesta = get("/api/v1/diccionarios/ES/palabras/mel%C3%B3n");

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains("Persona con pocas luces."), respuesta.body());
    }

    @Test
    @DisplayName("Contexto: el servidor arrancado | Acción: pedir la especificación OpenAPI | Resultado esperado: 200, con el título del API")
    void publicaLaEspecificacion() throws Exception {
        HttpResponse<String> respuesta = get("/v3/api-docs");

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains("Servidor de diccionarios"), respuesta.body());
    }
}
