package com.curso.diccionario.impl.bbdd;

import java.util.List;

import com.curso.diccionario.api.Significado;

// Copia sin JPA de lo leído: lo que sale del almacén no puede ser una entidad, porque fuera de la
// transacción sus colecciones perezosas fallarían al recorrerlas.
record SignificadoEnBBDD(String texto, List<String> ejemplos) implements Significado {

    SignificadoEnBBDD {
        ejemplos = List.copyOf(ejemplos);
    }

    @Override
    public String getTexto() {
        return texto;
    }

    @Override
    public List<String> getEjemplos() {
        return ejemplos;
    }
}
