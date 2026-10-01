package com.curso.diccionario.ui.consola.api;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Significado;

// Todo lo que la aplicación le dice o le pide al usuario. La lógica (qué decir y cuándo) no está
// aquí: está en quien la usa. Así se puede cambiar cómo se habla con el usuario (argumentos,
// teclado, otro idioma) sin tocar la lógica, y probar la lógica sin capturar la consola.
public interface InterfazDeUsuario {

    // Vacío si el usuario no lo ha dado: decidir qué hacer entonces es lógica, no interfaz.
    Optional<String> obtenerIdiomaDelUsuario();

    Optional<String> obtenerPalabraDelUsuario();

    void mostrarSignificadosDePalabra(String idioma, String palabra, List<Significado> significados);

    void mostrarPalabraNoEncontrada(String idioma, String palabra);

    void mostrarIdiomaNoEncontrado(String idioma);

    void mostrarErrorDeUsoDelPrograma();

    void mostrarErrorGenerico(Exception error);
}
