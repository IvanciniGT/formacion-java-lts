package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra, SuministradorDeDiccionarios suministrador) {
        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionario(idioma);
        if (potencialDiccionario.isEmpty()) {
            System.out.println("Lo siento, pero no tengo diccionario para el idioma " + idioma + ".");
            return;
        }

        Optional<List<Significado>> potencialesSignificados = potencialDiccionario.get().getSignificados(palabra);
        if (potencialesSignificados.isEmpty()) {
            System.out.println("La palabra " + palabra + " NO existe en el idioma " + idioma + ".");
            return;
        }

        System.out.println("La palabra " + palabra + " existe en el idioma " + idioma + " y tiene los siguientes significados:");
        for (Significado significado : potencialesSignificados.get()) {
            System.out.println("- " + significado.getTexto());
            for (String ejemplo : significado.getEjemplos()) {
                System.out.println("    Ej: " + ejemplo);
            }
        }
    }

}
