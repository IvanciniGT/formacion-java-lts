package com.curso.diccionario.impl.bbdd;

import java.io.IOException;
import java.util.List;

// De dónde salen los ficheros que se cargan. Una interfaz para que las pruebas pongan los suyos;
// en la aplicación son los jars de diccionario-es, diccionario-en... del classpath.
public interface FuenteDeDiccionarios {

    List<String> idiomas() throws IOException;

    byte[] contenido(String idioma) throws IOException;
}
