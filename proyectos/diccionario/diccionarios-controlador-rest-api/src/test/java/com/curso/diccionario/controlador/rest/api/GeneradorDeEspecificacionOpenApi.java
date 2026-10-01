package com.curso.diccionario.controlador.rest.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

// No es una prueba: lo lanza Maven al empaquetar (ver el pom) para meter la spec en el jar.
// springdoc solo sabe generarla desde una aplicación en marcha, y el API no tiene implementación:
// se levanta una vacía, se le pide la spec y se apaga. Las peticiones de negocio no se hacen nunca.
@SpringBootConfiguration
@EnableAutoConfiguration
@Import(GeneradorDeEspecificacionOpenApi.ControladorVacio.class)
public class GeneradorDeEspecificacionOpenApi {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Uso: GeneradorDeEspecificacionOpenApi <fichero de destino>...");
        }

        try (ConfigurableApplicationContext contexto = new SpringApplicationBuilder(GeneradorDeEspecificacionOpenApi.class)
                // Puerto 0: que el sistema dé uno libre; un puerto fijo rompería el build si está ocupado.
                .properties("server.port=0", "spring.main.banner-mode=off", "logging.level.root=warn")
                .run()) {
            String puerto = contexto.getEnvironment().getRequiredProperty("local.server.port");
            HttpResponse<String> respuesta = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/v3/api-docs.yaml")).build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (respuesta.statusCode() != 200) {
                throw new IllegalStateException("springdoc respondió " + respuesta.statusCode() + ": " + respuesta.body());
            }
            for (String fichero : args) {
                Path destino = Path.of(fichero);
                Files.createDirectories(destino.getParent());
                Files.writeString(destino, respuesta.body(), StandardCharsets.UTF_8);
                System.out.println("Especificación OpenAPI generada en " + destino);
            }
        }
    }

    @RestController
    static class ControladorVacio implements ApiRestDiccionarios {

        @Override
        public List<String> listarIdiomas() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DiccionarioDTO obtenerDiccionario(String idioma) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PalabraDTO buscarPalabra(String idioma, String palabra) {
            throw new UnsupportedOperationException();
        }
    }
}
