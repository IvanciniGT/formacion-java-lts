package com.curso.diccionario.ui.consola.argumentos;

import java.util.List;

import com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Constructor público sin argumentos: es lo que necesita el ServiceLoader.
public class FabricaDeInterfazPorArgumentos implements FabricaDeInterfacesDeUsuario {

    @Override
    public InterfazDeUsuario crear(List<String> argumentosDelPrograma) {
        return new InterfazDeUsuarioPorArgumentos(argumentosDelPrograma, System.out);
    }
}
