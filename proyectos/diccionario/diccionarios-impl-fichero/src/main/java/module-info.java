module impl.diccionarios.en.ficheros {
    requires api.diccionarios;
    requires static lombok;

    // No exporta nada: nadie salvo el ServiceLoader puede crear instancias de sus clases.
    provides com.curso.diccionario.api.SuministradorDeDiccionarios
            with com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros;
}
