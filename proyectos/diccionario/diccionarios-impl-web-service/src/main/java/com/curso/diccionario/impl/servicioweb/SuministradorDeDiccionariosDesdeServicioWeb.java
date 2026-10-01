package com.curso.diccionario.impl.servicioweb;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.controlador.rest.api.CodigoDeError;
import com.google.gson.reflect.TypeToken;

import lombok.NonNull;

// No guarda nada: cada pregunta es una petición al servidor de diccionarios,
// que es quien tiene los ficheros y la cache.
public class SuministradorDeDiccionariosDesdeServicioWeb implements SuministradorDeDiccionarios {

    static final String VARIABLE_DE_ENTORNO_SERVIDOR = "DICCIONARIOS_SERVIDOR";
    static final String SERVIDOR_POR_DEFECTO = "http://localhost:8080";

    private static final Type LISTA_DE_IDIOMAS = new TypeToken<List<String>>() {}.getType();

    private final ClienteDelServidorDeDiccionarios cliente;

    public SuministradorDeDiccionariosDesdeServicioWeb(String servidor) {
        this.cliente = new ClienteDelServidorDeDiccionarios(servidor);
    }

    // El ServiceLoader solo sabe instanciar con un constructor sin argumentos o con este método;
    // así el constructor puede seguir pidiendo el servidor.
    public static SuministradorDeDiccionarios provider() {
        String servidorDeclarado = System.getenv(VARIABLE_DE_ENTORNO_SERVIDOR);
        return new SuministradorDeDiccionariosDesdeServicioWeb(servidorDeclarado != null ? servidorDeclarado : SERVIDOR_POR_DEFECTO);
    }

    // Esta se borrará!
    @Override
    public boolean tienesDiccionarioDe(@NonNull String idioma) {
        try {
            return tienesDiccionarioDeIdioma(idioma);
        } catch (Exception e) {
            return false;
        }
    }

    // Esta se borrará!
    @Override
    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        try {
            return getDiccionarioBuena(idioma);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> getIdiomas() {
        try {
            HttpResponse<String> respuesta = cliente.get(cliente.rutaDeDiccionarios());
            // Sin mirar el status, el JSON de un error se intentaría leer como lista de idiomas.
            if (respuesta.statusCode() != 200) {
                throw cliente.respuestaInesperada(respuesta);
            }
            return cliente.leer(respuesta, LISTA_DE_IDIOMAS);
        } catch (ErrorDelServidorDeDiccionariosException e) {
            System.err.println("Error al obtener los idiomas: " + e.getMessage());
            // TODO: Modificar api para lanzar una excepción en lugar de devolver una lista vacía
            return List.of();
        }
    }

    @Override
    public boolean tienesDiccionarioDeIdioma(@NonNull String idioma) throws Exception {
        HttpResponse<String> respuesta = cliente.get(cliente.rutaDeDiccionario(idioma));
        if (respuesta.statusCode() == 200) {
            return true;
        }
        if (respuesta.statusCode() == 404
                && cliente.codigoDeError(respuesta).filter(CodigoDeError.IDIOMA_NO_DISPONIBLE::equals).isPresent()) {
            return false;
        }
        throw cliente.respuestaInesperada(respuesta);
    }

    @Override
    public Optional<Diccionario> getDiccionarioBuena(@NonNull String idioma) throws Exception {
        if (!tienesDiccionarioDeIdioma(idioma)) {
            return Optional.empty();
        }
        return Optional.of(new DiccionarioDesdeServicioWeb(idioma, cliente));
    }

}
