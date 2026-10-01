package com.curso.diccionario.impl.bbdd;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.curso.diccionario.impl.ficheros.formato.FormatoDeFicheroDeDiccionario;

// Al arrancar, deja la BBDD igual que los ficheros, pero sin recargar lo que no ha cambiado:
//  - fichero con la misma huella que la guardada: no se toca;
//  - huella distinta o idioma nuevo: se borran sus palabras y se cargan las del fichero;
//  - idioma en BBDD cuyo jar ya no está: se borra. Los idiomas disponibles son los jars que hay.
public class CargadorDeDiccionarios implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CargadorDeDiccionarios.class);

    private final FuenteDeDiccionarios fuente;
    private final AlmacenDeDiccionarios almacen;

    public CargadorDeDiccionarios(FuenteDeDiccionarios fuente, AlmacenDeDiccionarios almacen) {
        this.fuente = fuente;
        this.almacen = almacen;
    }

    @Override
    public void run(ApplicationArguments argumentos) throws IOException {
        cargar();
    }

    // Devuelve los idiomas que ha tenido que (re)cargar: así las pruebas ven si la huella evitó la recarga.
    public List<String> cargar() throws IOException {
        List<String> enLaFuente = fuente.idiomas();
        List<String> recargados = new ArrayList<>();
        for (String idioma : enLaFuente) {
            byte[] contenido = fuente.contenido(idioma);
            String huella = sha256(contenido);
            if (almacen.huellaDe(idioma).filter(huella::equals).isPresent()) {
                log.info("Diccionario {} sin cambios (huella {}): no se recarga", idioma, huella);
                continue;
            }
            almacen.recargar(idioma, huella, FormatoDeFicheroDeDiccionario.leer(contenido));
            recargados.add(idioma);
            log.info("Diccionario {} cargado en BBDD (huella {})", idioma, huella);
        }
        for (String idioma : almacen.idiomas()) {
            if (!enLaFuente.contains(idioma)) {
                almacen.borrar(idioma);
                log.info("Diccionario {} borrado de BBDD: ya no hay fichero", idioma);
            }
        }
        return recargados;
    }

    static String sha256(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (NoSuchAlgorithmException e) {
            // Todo JDK está obligado a traer SHA-256: si falta, el entorno está roto.
            throw new IllegalStateException(e);
        }
    }
}
