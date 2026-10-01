package com.curso.diccionario.impl.bbdd;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.impl.bbdd.modelo.HuellaDeFichero;
import com.curso.diccionario.impl.bbdd.modelo.PalabraEntidad;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDeHuellas;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDePalabras;
import com.curso.diccionario.impl.bbdd.modelo.SignificadoEntidad;

// Todo el acceso a la BBDD pasa por aquí, cada operación en su transacción.
// Por defecto de solo lectura; las que escriben lo dicen.
@Transactional(readOnly = true)
public class AlmacenDeDiccionarios {

    private final RepositorioDePalabras palabras;
    private final RepositorioDeHuellas huellas;

    public AlmacenDeDiccionarios(RepositorioDePalabras palabras, RepositorioDeHuellas huellas) {
        this.palabras = palabras;
        this.huellas = huellas;
    }

    public List<String> idiomas() {
        return huellas.findAll().stream().map(HuellaDeFichero::getIdioma).sorted().toList();
    }

    public boolean tieneIdioma(String idioma) {
        return huellas.existsById(idioma);
    }

    public Optional<String> huellaDe(String idioma) {
        return huellas.findById(idioma).map(HuellaDeFichero::getSha256);
    }

    public boolean existe(String idioma, String palabra) {
        return palabras.existsByIdiomaAndTexto(idioma, palabra);
    }

    public Optional<List<Significado>> significados(String idioma, String palabra) {
        // Se copian dentro de la transacción, mientras las colecciones perezosas aún se pueden leer.
        return palabras.findByIdiomaAndTexto(idioma, palabra)
                .map(encontrada -> encontrada.getSignificados().stream()
                        .<Significado>map(significado -> new SignificadoEnBBDD(significado.getTexto(), significado.getEjemplos()))
                        .toList());
    }

    // Todo en una transacción: nadie ve el idioma a medio cargar, y si algo falla queda el de antes.
    @Transactional
    public void recargar(String idioma, String sha256, Map<String, List<Significado>> palabrasConSignificados) {
        borrarPalabrasDe(idioma);
        palabras.saveAll(palabrasConSignificados.entrySet().stream()
                .map(entrada -> new PalabraEntidad(idioma, entrada.getKey(), entrada.getValue().stream()
                        .map(significado -> new SignificadoEntidad(significado.getTexto(), significado.getEjemplos()))
                        .toList()))
                .toList());
        huellas.save(new HuellaDeFichero(idioma, sha256, Instant.now()));
    }

    @Transactional
    public void borrar(String idioma) {
        borrarPalabrasDe(idioma);
        huellas.deleteById(idioma);
    }

    private void borrarPalabrasDe(String idioma) {
        palabras.borrarEjemplosDe(idioma);
        palabras.borrarSignificadosDe(idioma);
        palabras.borrarPalabrasDe(idioma);
    }
}
