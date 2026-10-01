package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;

import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;

public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra, SuministradorDeDiccionarios suministrador) throws Exception {
        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionarioBuena(idioma);
        if (potencialDiccionario.isEmpty()) {
            System.out.println("Lo siento, pero no tengo diccionario para el idioma " + idioma + ".");
            return;
        }
        ResultadoDeBusquedaDePalabra resultado = potencialDiccionario.get().buscarPalabra(palabra);

        // Resultado de búsqueda es un sealed interface que puede ser implementado por: PalabraEncontrada(List<Significados> significados), PalabraNoEncontrada, ErrorEnLaBusqueda(Exception error)
/* Esto es Java 21: Patter Matching
        switch(resultado){
            case PalabraEncontrada pf -> {
                List<Significado> potencialesSignificados = pf.significados();
                System.out.println("La palabra " + palabra + " existe en el idioma " + idioma + " y tiene los siguientes significados:");
                for (Significado significado : potencialesSignificados) {
                    System.out.println("- " + significado.getTexto());
                    for (String ejemplo : significado.getEjemplos()) {
                        System.out.println("    Ej: " + ejemplo);
                    }
                }
            }
            case PalabraNoEncontrada pn -> {
                System.out.println("La palabra " + palabra + " NO existe en el idioma " + idioma + ".");
            }
            case ErrorEnLaBusquedaDePalabra ee -> {
                throw ee.error();
            }
        }
*/
        // Como estamos en Java 17, lo hacemos con instanceof y casting
        // Es mucho peor.
        // Pattern matching nos avisaría si no hemos cubierto todos los casos posibles.
        // Los ifs no nos avisan si falta cubrir algún caso.
        if (resultado instanceof PalabraEncontrada pf) {
            List<Significado> potencialesSignificados = pf.significados();
            System.out.println("La palabra " + palabra + " existe en el idioma " + idioma + " y tiene los siguientes significados:");
            for (Significado significado : potencialesSignificados) {
                System.out.println("- " + significado.getTexto());
                for (String ejemplo : significado.getEjemplos()) {
                    System.out.println("    Ej: " + ejemplo);
                }
            }
        } else if (resultado instanceof PalabraNoEncontrada pn) {
            System.out.println("La palabra " + palabra + " NO existe en el idioma " + idioma + ".");
        } else if (resultado instanceof ErrorEnLaBusquedaDePalabra ee) {
            throw ee.error();
        }

    }

}
