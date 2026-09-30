module app {
    requires api.diccionarios;

    // Necesita que alguien, en tiempo de ejecución, aporte una implementación.
    uses com.curso.diccionario.api.SuministradorDeDiccionarios;

    // Los diccionarios van en este jar pero los lee la implementación, que es otro módulo:
    // sin abrir el paquete, el class loader no le entrega recursos de un módulo ajeno.
    opens diccionarios;
}
