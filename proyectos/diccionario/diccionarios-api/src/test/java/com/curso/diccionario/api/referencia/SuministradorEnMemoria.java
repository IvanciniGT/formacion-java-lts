package com.curso.diccionario.api.referencia;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;

import lombok.NonNull;

/**
 * La implementación más tonta posible del API. Solo existe para demostrar que las pruebas
 * de contrato se pueden cumplir: si esta falla, el defecto está en las pruebas.
 */
class SuministradorEnMemoria implements SuministradorDeDiccionarios {

    private final Map<String, Map<String, List<SignificadoDePrueba>>> diccionarios;

    SuministradorEnMemoria(DatosDePrueba datos) {
        this.diccionarios = datos.getDiccionarios();
    }

    @Override
    public List<String> getIdiomas() {
        return List.copyOf(diccionarios.keySet());
    }

    @Override
    public boolean tienesDiccionarioDe(@NonNull String idioma) {
        return diccionarios.containsKey(idioma);
    }

    @Override
    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        return Optional.ofNullable(diccionarios.get(idioma)).map(palabras -> new DiccionarioEnMemoria(idioma, palabras));
    }

    private record DiccionarioEnMemoria(String idioma, Map<String, List<SignificadoDePrueba>> palabras) implements Diccionario {

        @Override
        public String getIdioma() {
            return idioma;
        }

        @Override
        public boolean existe(@NonNull String palabra) {
            return palabras.containsKey(palabra);
        }

        @Override
        public Optional<List<Significado>> getSignificados(@NonNull String palabra) {
            return Optional.ofNullable(palabras.get(palabra))
                    .map(significados -> significados.stream().<Significado>map(SignificadoEnMemoria::new).toList());
        }
    }

    private record SignificadoEnMemoria(SignificadoDePrueba datos) implements Significado {

        @Override
        public String getTexto() {
            return datos.texto();
        }

        @Override
        public List<String> getEjemplos() {
            return datos.ejemplos();
        }
    }

}
