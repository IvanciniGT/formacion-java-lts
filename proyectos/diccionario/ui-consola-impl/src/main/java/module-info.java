module impl.ui.consola.argumentos {
    requires api.ui.consola;

    // No exporta nada: solo se llega a ella por el ServiceLoader.
    provides com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario
            with com.curso.diccionario.ui.consola.argumentos.FabricaDeInterfazPorArgumentos;
}
