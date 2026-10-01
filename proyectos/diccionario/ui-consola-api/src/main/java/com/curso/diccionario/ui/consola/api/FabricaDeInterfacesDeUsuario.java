package com.curso.diccionario.ui.consola.api;

import java.util.List;

// Lo que se carga con el ServiceLoader. Hace falta una fábrica porque el ServiceLoader crea la
// instancia sin argumentos, y la interfaz de usuario necesita los del programa.
public interface FabricaDeInterfacesDeUsuario {

    InterfazDeUsuario crear(List<String> argumentosDelPrograma);
}
