package com.curso.diccionario.impl.servicioweb;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.net.httpserver.HttpServer;

// Un servidor HTTP de verdad (el del JDK, sin dependencias) que responde lo que cada prueba le diga.
// Así se prueba el cliente entero, HTTP incluido, sin depender del servidor real ni de Spring.
final class ServidorFalso implements AutoCloseable {

    record Respuesta(int status, String cuerpo) {}

    private final HttpServer servidor;
    private final Map<String, Respuesta> respuestas = new ConcurrentHashMap<>();
    final List<String> rutasPedidasSinDecodificar = new CopyOnWriteArrayList<>();

    ServidorFalso() throws IOException {
        // Puerto 0: el que el sistema dé libre.
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            rutasPedidasSinDecodificar.add(intercambio.getRequestURI().getRawPath());
            Respuesta respuesta = respuestas.getOrDefault(intercambio.getRequestURI().getPath(), new Respuesta(404, "<html>no existe</html>"));
            byte[] cuerpo = respuesta.cuerpo().getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(respuesta.status(), cuerpo.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(cuerpo);
            }
        });
        servidor.start();
    }

    // La ruta va decodificada: "melón", no "mel%C3%B3n".
    ServidorFalso responde(String ruta, int status, String cuerpo) {
        respuestas.put(ruta, new Respuesta(status, cuerpo));
        return this;
    }

    String url() {
        return "http://localhost:" + servidor.getAddress().getPort();
    }

    @Override
    public void close() {
        servidor.stop(0);
    }
}
