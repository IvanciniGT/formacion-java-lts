package com.curso.diccionario.impl.ficheros.formato;

import java.util.List;

import com.curso.diccionario.api.Significado;

public record SignificadoDesdeFichero(String texto, List<String> ejemplos) implements Significado {

    // El record genera texto() y ejemplos(); la interfaz pide los nombres con get.
    @Override
    public String getTexto() {
        return texto;
    }

    @Override
    public List<String> getEjemplos() {
        return ejemplos;
    }

}
