module app {
    requires api.diccionarios;
    requires api.ui.consola;

    // Necesita que alguien, en tiempo de ejecución, aporte una implementación de cada cosa.
    uses com.curso.diccionario.api.SuministradorDeDiccionarios;
    uses com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;

    // Ya no lleva diccionarios (ni opens diccionarios): vienen en sus propios jars, diccionario-es, diccionario-en...
}
