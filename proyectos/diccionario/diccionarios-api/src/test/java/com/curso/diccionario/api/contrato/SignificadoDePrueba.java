package com.curso.diccionario.api.contrato;

import java.util.List;

import com.curso.diccionario.api.Significado;

// Significado no define equals: para comparar lo que devuelve una implementación
// con lo esperado se traduce todo a este record, que sí lo tiene.
public record SignificadoDePrueba(String texto, List<String> ejemplos) {

    public SignificadoDePrueba {
        ejemplos = List.copyOf(ejemplos);
    }

    public static SignificadoDePrueba significado(String texto, String... ejemplos) {
        return new SignificadoDePrueba(texto, List.of(ejemplos));
    }

    public static SignificadoDePrueba de(Significado significado) {
        return new SignificadoDePrueba(significado.getTexto(), significado.getEjemplos());
    }

}
