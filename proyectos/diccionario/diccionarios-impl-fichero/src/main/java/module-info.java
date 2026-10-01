module impl.diccionarios.en.ficheros {
    requires api.diccionarios;
    requires static lombok;

    // Las clases del suministrador no se exportan: nadie salvo el ServiceLoader puede crear instancias.
    provides com.curso.diccionario.api.SuministradorDeDiccionarios
            with com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros;

    // El formato de fichero y dónde están los diccionarios en el classpath sí: los necesita
    // cualquiera que cargue estos ficheros en otro sitio (la implementación en BBDD).
    exports com.curso.diccionario.impl.ficheros.formato;
}
