package com.curso.diccionario.impl.servicioweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ServerSocket;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.ErrorEnLaBusquedaDePalabra;
import com.curso.diccionario.api.PalabraEncontrada;
import com.curso.diccionario.api.PalabraNoEncontrada;
import com.curso.diccionario.api.ResultadoDeBusquedaDePalabra;
import com.curso.diccionario.api.Significado;

// Lo que el contrato no puede probar porque el servidor real nunca lo hace: errores, respuestas raras,
// servidor caído. Que el cliente cumple el contrato contra el servidor real se prueba en
// diccionario-app-servidor (ServicioWebCumpleContratoTest).
class SuministradorDeDiccionariosDesdeServicioWebTest {

    static final String DICCIONARIOS = "/api/v1/diccionarios";
    static final String IDIOMA_NO_DISPONIBLE = "{\"codigo\":\"IDIOMA_NO_DISPONIBLE\",\"mensaje\":\"No hay\"}";
    static final String PALABRA_NO_ENCONTRADA = "{\"codigo\":\"PALABRA_NO_ENCONTRADA\",\"mensaje\":\"No está\"}";
    static final String ERROR_INTERNO = "{\"codigo\":\"ERROR_INTERNO\",\"mensaje\":\"Fallo\"}";

    ServidorFalso servidor;
    SuministradorDeDiccionariosDesdeServicioWeb suministrador;

    @BeforeEach
    void arrancar() throws Exception {
        servidor = new ServidorFalso();
        suministrador = new SuministradorDeDiccionariosDesdeServicioWeb(servidor.url());
    }

    @AfterEach
    void parar() {
        servidor.close();
    }

    private Diccionario espanol() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES", 200, "{\"idioma\":\"ES\"}");
        return suministrador.getDiccionarioBuena("ES").orElseThrow();
    }

    // ---------------------------------------------------------------- idiomas

    @Test
    @DisplayName("Contexto: el servidor anuncia ES y EN | Acción: pedir los idiomas | Resultado esperado: ES y EN")
    void leeLosIdiomas() throws Exception {
        servidor.responde(DICCIONARIOS, 200, "[\"ES\",\"EN\"]");

        assertEquals(List.of("ES", "EN"), suministrador.getIdiomasDisponibles());
    }

    @Test
    @DisplayName("Contexto: el servidor responde 500 a los idiomas | Acción: pedirlos | Resultado esperado: el nuevo lanza excepción; el antiguo, lista vacía")
    void unErrorAlPedirLosIdiomasSeAvisa() {
        servidor.responde(DICCIONARIOS, 500, ERROR_INTERNO);

        assertThrows(ErrorDelServidorDeDiccionariosException.class, () -> suministrador.getIdiomasDisponibles());
        assertEquals(List.of(), suministrador.getIdiomas());
    }

    // ---------------------------------------------------------- diccionarios

    @Test
    @DisplayName("Contexto: el servidor responde 404 IDIOMA_NO_DISPONIBLE | Acción: preguntar por XX | Resultado esperado: false")
    void unIdiomaQueElServidorNoTiene() throws Exception {
        servidor.responde(DICCIONARIOS + "/XX", 404, IDIOMA_NO_DISPONIBLE);

        assertFalse(suministrador.tienesDiccionarioDeIdioma("XX"));
    }

    @Test
    @DisplayName("Contexto: un 404 sin código de error (la URL del servidor está mal) | Acción: preguntar por ES | Resultado esperado: excepción, no 'no hay idioma'")
    void unCuatrocientosCuatroAjenoNoEsQueNoHayaIdioma() {
        // ServidorFalso responde 404 con HTML a lo que no conoce: lo mismo que haría otro servidor cualquiera.
        assertThrows(ErrorDelServidorDeDiccionariosException.class, () -> suministrador.tienesDiccionarioDeIdioma("ES"));
    }

    // -------------------------------------------------------------- palabras

    @Test
    @DisplayName("Contexto: el servidor devuelve melón con 2 significados | Acción: buscar melón | Resultado esperado: PalabraEncontrada con los 2, en orden y con ejemplos")
    void leeLosSignificados() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/melón", 200, """
                {"idioma":"ES","palabra":"melón","significados":[
                  {"texto":"Fruto.","ejemplos":[]},
                  {"texto":"Persona con pocas luces.","ejemplos":["No seas melón"]}]}""");

        ResultadoDeBusquedaDePalabra resultado = espanol().buscarPalabra("melón");

        List<Significado> significados = assertInstanceOf(PalabraEncontrada.class, resultado).significados();
        assertEquals(List.of("Fruto.", "Persona con pocas luces."), significados.stream().map(Significado::getTexto).toList());
        assertEquals(List.of("No seas melón"), significados.get(1).getEjemplos());
    }

    @Test
    @DisplayName("Contexto: el servidor responde 404 PALABRA_NO_ENCONTRADA | Acción: buscar patata | Resultado esperado: PalabraNoEncontrada")
    void unaPalabraQueNoEsta() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/patata", 404, PALABRA_NO_ENCONTRADA);

        assertInstanceOf(PalabraNoEncontrada.class, espanol().buscarPalabra("patata"));
        assertFalse(espanol().existeLaPalabra("patata"));
    }

    @Test
    @DisplayName("Contexto: el servidor responde 500 al buscar | Acción: buscar melón | Resultado esperado: ErrorEnLaBusquedaDePalabra, y existeLaPalabra lanza")
    void unErrorDelServidorAlBuscar() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/melón", 500, ERROR_INTERNO);
        Diccionario espanol = espanol();

        assertInstanceOf(ErrorEnLaBusquedaDePalabra.class, espanol.buscarPalabra("melón"));
        assertThrows(ErrorDelServidorDeDiccionariosException.class, () -> espanol.existeLaPalabra("melón"));
    }

    @Test
    @DisplayName("Contexto: la palabra 'media naranja?' (espacio, tilde no, interrogación) | Acción: buscarla | Resultado esperado: viaja codificada como un solo segmento de la ruta")
    void laPalabraSeCodificaEnLaUrl() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/media naranja?", 404, PALABRA_NO_ENCONTRADA);

        ResultadoDeBusquedaDePalabra resultado = espanol().buscarPalabra("media naranja?");

        // Sin codificar, el espacio rompería la URL y la ? empezaría una query: se buscaría "media".
        assertInstanceOf(PalabraNoEncontrada.class, resultado);
        assertTrue(servidor.rutasPedidasSinDecodificar.contains(DICCIONARIOS + "/ES/palabras/media%20naranja%3F"),
                servidor.rutasPedidasSinDecodificar.toString());
    }

    @Test
    @DisplayName("Contexto: no hay nadie escuchando en el puerto | Acción: preguntar por ES | Resultado esperado: excepción de conexión")
    void conElServidorCaido() throws Exception {
        int puertoLibre;
        try (ServerSocket socket = new ServerSocket(0)) {
            puertoLibre = socket.getLocalPort();
        }
        SuministradorDeDiccionariosDesdeServicioWeb sinServidor = new SuministradorDeDiccionariosDesdeServicioWeb("http://localhost:" + puertoLibre);

        ErrorDelServidorDeDiccionariosException error = assertThrows(ErrorDelServidorDeDiccionariosException.class,
                () -> sinServidor.tienesDiccionarioDeIdioma("ES"));
        assertTrue(error.getMessage().startsWith("No se pudo conectar"), error.getMessage());
    }
}
