package com.curso.diccionario.impl.bbdd;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.contrato.DatosDePrueba;

// Lleva el "Dado que..." de las pruebas de contrato a la BBDD por el mismo camino que el cargador.
final class CargaDeDatosDePrueba {

    private CargaDeDatosDePrueba() {
    }

    static void cargar(AlmacenDeDiccionarios almacen, DatosDePrueba datos) {
        almacen.idiomas().forEach(almacen::borrar);
        datos.getDiccionarios().forEach((idioma, palabras) -> {
            Map<String, List<Significado>> convertidas = new LinkedHashMap<>();
            palabras.forEach((palabra, significados) -> convertidas.put(palabra, significados.stream()
                    .<Significado>map(significado -> new SignificadoEnBBDD(significado.texto(), significado.ejemplos()))
                    .toList()));
            almacen.recargar(idioma, "huella-de-prueba", convertidas);
        });
    }
}
