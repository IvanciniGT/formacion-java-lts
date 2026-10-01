package com.curso.diccionario.impl.ficheros.formato;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.curso.diccionario.api.Significado;

// Una línea por palabra:   palabra=significado1(ejemplo1)(ejemplo2)|significado2(ejemplo1)|significado3
// Público: la implementación en BBDD carga estos mismos ficheros y no debe tener su propia versión del formato.
//
// Interfaz y no clase de utilidades (final + constructor privado): desde Java 8 una interfaz puede tener
// métodos estáticos, y una interfaz ya no se puede instanciar, así que no hace falta el truco del constructor.
// Ojo: los estáticos de una interfaz NO se heredan. Quien la implementara no podría llamar a leer(...)
// a secas: siempre FormatoDeFicheroDeDiccionario.leer(...). Y tampoco hay nada que implementar: es solo
// un sitio donde agrupar funciones.
// "public" sobra (en una interfaz todo método es público salvo que diga private): se deja para que se lea.
public interface FormatoDeFicheroDeDiccionario {

    // URL y no Path: un fichero dentro de un jar no es un Path del disco, pero sí tiene URL.
    public static byte[] leerBytes(URL fichero) throws IOException {
        try (InputStream entrada = fichero.openStream()) {
            return entrada.readAllBytes();
        }
    }

    public static Map<String, List<Significado>> leer(URL fichero) throws IOException {
        return leer(leerBytes(fichero));
    }

    public static Map<String, List<Significado>> leer(byte[] contenido) {
        List<String> lineas = new String(contenido, StandardCharsets.UTF_8).lines().toList();
        return lineas                                                                                                          // Las lineas del fichero
            .stream()                                                                                                          // Para cada linea
            .filter( linea -> !linea.trim().isEmpty() )                                                                        // Quito las lineas en blanco
            //.filter( linea -> linea.contains("=") )                                                                          // Para evitar problemas, requiero que las lineas tengan un =
                        // Esto es una ñapa... se come silenciosamente un error de sintaxis del fichero de diccionario
            .collect(Collectors.toMap(                                                                                         // Convierto enentradas de un mapa
                linea -> linea.split("=")[0] ,                                                                                 // Cuya clave es la palabra (lo de antes del "=")
                                                                                                                               // Cuyo valor es una lista de significados
                linea -> Arrays.stream(linea.split("=")[1].split("\\|"))                                                       // Cojo lo de detras del = y separo por |
                                .map(                                                                                          // Transformo ese array
                                    textoConEjemplos -> {
                                        String[] partes                 = textoConEjemplos.split("\\(|\\)");                   // Partiendo para cada item por ()
                                        String texto                    = partes[0].trim();                                    // Lo de antes de los () es el significado
                                        List<String> listadoEjemplos    = Arrays.stream(partes).skip(1)                        // Lo de detras de los () son los ejemplos
                                                                              .filter(ejemplo -> !ejemplo.isBlank())   // ")(" deja un trozo vacío entre ejemplo y ejemplo
                                                                              .collect(Collectors.toList());
                                        return (Significado) new SignificadoDesdeFichero(texto, listadoEjemplos);      // que uso para crear el Objeto SignificadoDesdeFichero
                                    }
                                )
                                .collect(Collectors.toList())                                                               // al final, entrego los significados como una lista
            ));
    }

}


// Tenemos que leer el fichero del diccionario de turno.
// Linea a linea...
// para cada linea, hacer un split por =
// Lo primero será la palabra... lo siguiente (otro string) los significados.
// esos significados (string) los partimos con otro split por |
// cada trozo (string) será un significado.
// tenbemos que hacer un nuevo split por (), para sacar los ejemplos
// Lo primero es el texto del significado.
// Los bloques siguientes en el split serán los ejemplos.
// Necesitamos generar objetos de tipo Significado.
// E irempaquetando todos e una lista... para cada palabra
// ir generando un Map<String, List<Significado>>

// Cuántas lineas de código necesito para hacer esto? Stamements
//    - UNA LINEA... Un statement... No he dicho que vaya a ser facil!
// Para ello vamos a usar Streams.

// Pero.., qué son los streams?
