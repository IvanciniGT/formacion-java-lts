package com.curso.diccionario.ui.consola.argumentos;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Lee idioma y palabra de los argumentos de línea de comandos. Podría pedírselos al usuario
// por teclado: para eso los obtener... están en la interfaz, aunque esta no pregunte nada.
public class InterfazDeUsuarioPorArgumentos implements InterfazDeUsuario {

    private final List<String> argumentos;
    // Recibida y no System.out directamente: así las pruebas leen lo que se escribe.
    private final PrintStream salida;

    public InterfazDeUsuarioPorArgumentos(List<String> argumentos, PrintStream salida) {
        this.argumentos = List.copyOf(argumentos);
        this.salida = salida;
    }

    @Override
    public Optional<String> obtenerIdiomaDelUsuario() {
        return argumento(0);
    }

    @Override
    public Optional<String> obtenerPalabraDelUsuario() {
        return argumento(1);
    }

    @Override
    public void mostrarSignificadosDePalabra(String idioma, String palabra, List<Significado> significados) {
        salida.println("La palabra " + palabra + " existe en el idioma " + idioma + " y tiene los siguientes significados:");
        for (Significado significado : significados) {
            salida.println("- " + significado.getTexto());
            for (String ejemplo : significado.getEjemplos()) {
                salida.println("    Ej: " + ejemplo);
            }
        }
    }

    @Override
    public void mostrarPalabraNoEncontrada(String idioma, String palabra) {
        salida.println("La palabra " + palabra + " NO existe en el idioma " + idioma + ".");
    }

    @Override
    public void mostrarIdiomaNoEncontrado(String idioma) {
        salida.println("Lo siento, pero no tengo diccionario para el idioma " + idioma + ".");
    }

    @Override
    public void mostrarErrorDeUsoDelPrograma() {
        salida.println("""
                Faltan argumentos.
                La forma correcta de invocar el programa es:

                    $ buscarPalabra <IDIOMA> <PALABRA>

                Ejemplo:

                    $ buscarPalabra ES melón""");
    }

    @Override
    public void mostrarErrorGenerico(Exception error) {
        salida.println("Ocurrió un error al procesar la petición: " + error.getMessage());
        salida.println("Detalles del error:");
        error.printStackTrace(salida);
        salida.println("Inténtelo de nuevo más tarde.");
    }

    // Un argumento en blanco no es un dato: es como no haberlo dado.
    private Optional<String> argumento(int posicion) {
        return posicion < argumentos.size() ? Optional.of(argumentos.get(posicion)).filter(texto -> !texto.isBlank()) : Optional.empty();
    }
}
