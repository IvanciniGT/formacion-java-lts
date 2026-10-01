package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Toda la lógica de la aplicación, y nada de cómo se habla con el usuario: eso es la InterfazDeUsuario.
public class ProcesadorDePeticiones {

    // Códigos de salida del programa: un script que nos llame puede saber qué pasó sin leer la consola.
    public static final int TODO_BIEN = 0;
    public static final int ERROR_DE_USO = 1;
    public static final int ERROR_INTERNO = 2;

    private final InterfazDeUsuario interfaz;
    private final SuministradorDeDiccionarios suministrador;

    public ProcesadorDePeticiones(InterfazDeUsuario interfaz, SuministradorDeDiccionarios suministrador) {
        this.interfaz = interfaz;
        this.suministrador = suministrador;
    }

    public int procesarPeticion() {
        Optional<String> idioma = interfaz.obtenerIdiomaDelUsuario();
        Optional<String> palabra = interfaz.obtenerPalabraDelUsuario();
        if (idioma.isEmpty() || palabra.isEmpty()) {
            interfaz.mostrarErrorDeUsoDelPrograma();
            return ERROR_DE_USO;
        }
        try {
            buscar(idioma.get(), palabra.get());
            return TODO_BIEN;
        } catch (Exception e) {
            interfaz.mostrarErrorGenerico(e);
            return ERROR_INTERNO;
        }
    }

    private void buscar(String idioma, String palabra) throws Exception {
        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionarioBuena(idioma);
        if (potencialDiccionario.isEmpty()) {
            interfaz.mostrarIdiomaNoEncontrado(idioma);
            return;
        }
        ResultadoDeBusquedaDePalabra resultado = potencialDiccionario.get().buscarPalabra(palabra);

        // Resultado de búsqueda es un sealed interface que puede ser implementado por: PalabraEncontrada(List<Significados> significados), PalabraNoEncontrada, ErrorEnLaBusqueda(Exception error)
/* Esto es Java 21: Patter Matching
        switch(resultado){
            case PalabraEncontrada pf -> interfaz.mostrarSignificadosDePalabra(idioma, palabra, pf.significados());
            case PalabraNoEncontrada pn -> interfaz.mostrarPalabraNoEncontrada(idioma, palabra);
            case ErrorEnLaBusquedaDePalabra ee -> throw ee.error();
        }
*/
        // Como estamos en Java 17, lo hacemos con instanceof y casting
        // Es mucho peor.
        // Pattern matching nos avisaría si no hemos cubierto todos los casos posibles.
        // Los ifs no nos avisan si falta cubrir algún caso.
        if (resultado instanceof PalabraEncontrada pf) {
            List<Significado> potencialesSignificados = pf.significados();
            interfaz.mostrarSignificadosDePalabra(idioma, palabra, potencialesSignificados);
        } else if (resultado instanceof PalabraNoEncontrada) {
            interfaz.mostrarPalabraNoEncontrada(idioma, palabra);
        } else if (resultado instanceof ErrorEnLaBusquedaDePalabra ee) {
            throw ee.error();
        }
    }

}
