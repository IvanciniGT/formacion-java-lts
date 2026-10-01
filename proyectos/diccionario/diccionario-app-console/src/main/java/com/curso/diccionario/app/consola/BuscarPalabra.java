package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.ServiceLoader;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Sin lógica: solo monta las piezas y se las pasa al procesador.
public class BuscarPalabra {

    public static void main(String[] args) {
        // El único sitio que decide qué implementaciones se usan, y ni siquiera las nombra:
        // las que haya en el module path.
        InterfazDeUsuario interfaz = cargar(FabricaDeInterfacesDeUsuario.class).crear(List.of(args));
        SuministradorDeDiccionarios suministrador = cargar(SuministradorDeDiccionarios.class);

        System.exit(new ProcesadorDePeticiones(interfaz, suministrador).procesarPeticion());
    }

    private static <T> T cargar(Class<T> tipo) {
        return ServiceLoader.load(tipo)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay ninguna implementación de " + tipo.getSimpleName() + " en el module path"));
    }

}
