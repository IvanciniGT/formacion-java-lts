package com.curso.diccionario.impl.bbdd;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

public class SuministradorDeDiccionariosEnBBDD implements SuministradorDeDiccionarios {

    private static final Logger log = LoggerFactory.getLogger(SuministradorDeDiccionariosEnBBDD.class);

    private final AlmacenDeDiccionarios almacen;

    public SuministradorDeDiccionariosEnBBDD(AlmacenDeDiccionarios almacen) {
        this.almacen = almacen;
    }

    // Esta se borrará!
    @Override
    public List<String> getIdiomas() {
        try {
            return getIdiomasDisponibles();
        } catch (RuntimeException e) {
            log.warn("No se pudieron leer los idiomas de la BBDD", e);
            return List.of();
        }
    }

    @Override
    public List<String> getIdiomasDisponibles() {
        return almacen.idiomas();
    }

    // Esta se borrará!
    @Override
    public boolean tienesDiccionarioDe(String idioma) {
        // Fuera del try: un null es un error de quien llama, no "no tengo ese idioma".
        Objects.requireNonNull(idioma, "idioma");
        try {
            return tienesDiccionarioDeIdioma(idioma);
        } catch (RuntimeException e) {
            log.warn("No se pudo consultar el idioma {} en la BBDD", idioma, e);
            return false;
        }
    }

    @Override
    public boolean tienesDiccionarioDeIdioma(String idioma) {
        return almacen.tieneIdioma(normalizar(idioma));
    }

    // Esta se borrará!
    @Override
    public Optional<Diccionario> getDiccionario(String idioma) {
        Objects.requireNonNull(idioma, "idioma");
        try {
            return getDiccionarioBuena(idioma);
        } catch (RuntimeException e) {
            log.warn("No se pudo consultar el idioma {} en la BBDD", idioma, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Diccionario> getDiccionarioBuena(String idioma) {
        String normalizado = normalizar(idioma);
        if (!almacen.tieneIdioma(normalizado)) {
            return Optional.empty();
        }
        return Optional.of(new DiccionarioEnBBDD(normalizado, almacen));
    }

    // Los códigos se guardan en mayúsculas, como los da el índice de los jars: "es" también vale.
    private static String normalizar(String idioma) {
        return Objects.requireNonNull(idioma, "idioma").toUpperCase(Locale.ROOT);
    }
}
