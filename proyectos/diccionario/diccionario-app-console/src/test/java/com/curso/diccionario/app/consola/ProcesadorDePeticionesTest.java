package com.curso.diccionario.app.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// La razón de separar la interfaz de usuario: la lógica se prueba sin capturar la consola,
// mirando qué se le pidió mostrar a una interfaz falsa.
class ProcesadorDePeticionesTest {

    record SignificadoDePrueba(String getTexto, List<String> getEjemplos) implements Significado {}

    static final List<Significado> SIGNIFICADOS_DE_MELON = List.of(new SignificadoDePrueba("Fruto.", List.of()));

    // Apunta cada cosa que se le pide mostrar, como texto fácil de comparar.
    static class InterfazQueApunta implements InterfazDeUsuario {
        final List<String> mostrado = new ArrayList<>();
        final Optional<String> idioma;
        final Optional<String> palabra;

        InterfazQueApunta(String idioma, String palabra) {
            this.idioma = Optional.ofNullable(idioma);
            this.palabra = Optional.ofNullable(palabra);
        }

        @Override public Optional<String> obtenerIdiomaDelUsuario() { return idioma; }
        @Override public Optional<String> obtenerPalabraDelUsuario() { return palabra; }
        @Override public void mostrarSignificadosDePalabra(String i, String p, List<Significado> s) { mostrado.add("significados " + i + " " + p + " " + s.size()); }
        @Override public void mostrarPalabraNoEncontrada(String i, String p) { mostrado.add("palabra no encontrada " + i + " " + p); }
        @Override public void mostrarIdiomaNoEncontrado(String i) { mostrado.add("idioma no encontrado " + i); }
        @Override public void mostrarErrorDeUsoDelPrograma() { mostrado.add("error de uso"); }
        @Override public void mostrarErrorGenerico(Exception e) { mostrado.add("error " + e.getMessage()); }
    }

    // Un suministrador con un solo idioma (ES) cuyo diccionario responde siempre lo mismo.
    @SuppressWarnings("removal")
    static class SuministradorFijo implements SuministradorDeDiccionarios {
        final ResultadoDeBusquedaDePalabra resultado;

        SuministradorFijo(ResultadoDeBusquedaDePalabra resultado) {
            this.resultado = resultado;
        }

        @Override public List<String> getIdiomas() { return List.of("ES"); }
        @Override public boolean tienesDiccionarioDe(String idioma) { return "ES".equals(idioma); }
        @Override public Optional<Diccionario> getDiccionario(String idioma) {
            if (!"ES".equals(idioma)) {
                return Optional.empty();
            }
            return Optional.of(new Diccionario() {
                @Override public String getIdioma() { return "ES"; }
                @Override public boolean existe(String palabra) { return resultado instanceof PalabraEncontrada; }
                @Override public Optional<List<Significado>> getSignificados(String palabra) { return Optional.empty(); }
                @Override public ResultadoDeBusquedaDePalabra buscarPalabra(String palabra) { return resultado; }
            });
        }
    }

    private static int procesar(InterfazQueApunta interfaz, ResultadoDeBusquedaDePalabra resultado) {
        return new ProcesadorDePeticiones(interfaz, new SuministradorFijo(resultado)).procesarPeticion();
    }

    @Test
    @DisplayName("Contexto: ES melón, que existe | Acción: procesar | Resultado esperado: se muestran sus significados y sale con 0")
    void unaPalabraQueExisteMuestraSusSignificados() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "melón");

        int salida = procesar(interfaz, new PalabraEncontrada(SIGNIFICADOS_DE_MELON));

        assertEquals(List.of("significados ES melón 1"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.TODO_BIEN, salida);
    }

    @Test
    @DisplayName("Contexto: ES patata, que no existe | Acción: procesar | Resultado esperado: palabra no encontrada y sale con 0")
    void unaPalabraQueNoExiste() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "patata");

        int salida = procesar(interfaz, new PalabraNoEncontrada());

        assertEquals(List.of("palabra no encontrada ES patata"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.TODO_BIEN, salida);
    }

    @Test
    @DisplayName("Contexto: XX gato, sin diccionario de XX | Acción: procesar | Resultado esperado: idioma no encontrado")
    void unIdiomaSinDiccionario() {
        InterfazQueApunta interfaz = new InterfazQueApunta("XX", "gato");

        procesar(interfaz, new PalabraNoEncontrada());

        assertEquals(List.of("idioma no encontrado XX"), interfaz.mostrado);
    }

    @Test
    @DisplayName("Contexto: el usuario no da la palabra | Acción: procesar | Resultado esperado: error de uso, sale con 1 y no se busca nada")
    void sinPalabraEsUnErrorDeUso() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", null);

        int salida = procesar(interfaz, new PalabraNoEncontrada());

        assertEquals(List.of("error de uso"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.ERROR_DE_USO, salida);
    }

    @Test
    @DisplayName("Contexto: la búsqueda falla | Acción: procesar | Resultado esperado: error genérico con el motivo y sale con 2")
    void unErrorEnLaBusquedaSeMuestraComoError() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "melón");

        int salida = procesar(interfaz, new ErrorEnLaBusquedaDePalabra(new IOException("disco roto")));

        assertEquals(List.of("error disco roto"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.ERROR_INTERNO, salida);
    }
}
