module app {
    requires api.diccionarios;

    // Necesita que alguien, en tiempo de ejecución, aporte una implementación.
    uses com.curso.diccionario.api.SuministradorDeDiccionarios;
}
