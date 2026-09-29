package com.curso.diccionario.app.consola;

import java.util.ServiceLoader;

import com.curso.diccionario.api.SuministradorDeDiccionarios;

public class BuscarPalabra {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("""
                    Faltan argumentos.
                    La forma correcta de invocar el programa es:

                        $ buscarPalabra <IDIOMA> <PALABRA>

                    Ejemplo:

                        $ buscarPalabra ES melón""");
            System.exit(1);
        }

        // El único sitio que decide qué implementación se usa, y ni siquiera la nombra:
        // la que haya en el module path.
        SuministradorDeDiccionarios suministrador = ServiceLoader.load(SuministradorDeDiccionarios.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró un suministrador de diccionarios"));

        new ProcesadorDePeticiones().procesarPeticion(args[0], args[1], suministrador);
    }

}
