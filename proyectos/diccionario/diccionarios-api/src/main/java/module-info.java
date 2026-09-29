module api.diccionarios {
    // static: Lombok solo hace falta al compilar; @NonNull no llega al jar en tiempo de ejecución.
    requires static lombok;

    exports com.curso.diccionario.api;
}
