package com.curso.diccionario.impl.ficheros;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

import java.util.Map;
import java.util.WeakHashMap;

import lombok.NonNull;

// La lectura de los archivos la hacemos en modo LAZY (cuando se pida la primera vez un diccionario de un idioma se lee el archivo)
// Y se cachea en memoria.
// Siguientes peticiones hacen uso de cache!
// Para la cache, que estructura de datos podemos usar?
public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {

    static final String VARIABLE_DE_ENTORNO_CARPETA = "DICCIONARIOS_CARPETA";
    // Sin la variable, los diccionarios se buscan dentro de un jar del classpath, no en disco.
    static final String CARPETA_EN_CLASSPATH_POR_DEFECTO = "diccionarios/";

    private final String carpetaConFicherosDeDiccionarios;

    private Map<String, Diccionario> cacheDeDiccionarios = new WeakHashMap<>();
    // Un Map no es una buena elección para una cache.
    // El problema es que va engordando indefinidamente y nunca se eliminan entradas antiguas.
    // Puede acabar el sistema colapsando.
    // Una cache deberia poder eliminar datos si es necesario.
    // Hay una implementaciónd de Map que en caso de ser necesario elimina automáticamente las entradas.
    // Lo hace a petición del garbage collector. Si el garbage collector necesita memoria.
    // Podríamos usar otro tipo de map, con politicas de evition (como LRU - Least Recently Used, como los más antiguos se eliminan primero).
    // Para no complicarnos vamos a usar un WeakHashMap, que elimina automáticamente las entradas cuando las claves dejan de ser referenciadas.
    // WeakHashMap... lo que guarda son referencias débiles a las claves. Si un dato deja de ser referenciado en otro lugar, la entrada se elimina automáticamente cuando el garbage collector lo considere necesario.
    // Me quito del problema de gestionar la cache... Puede engordar un huevo... pero si es necesario, se eliminarán automáticamente las entradas viejas.
    // En java exste el concepto de WeakRefecence, que me permite crear variables que no impiden que el garbage collector elimine el objeto al que apuntan si no hay otras referencias fuertes.

    public SuministradorDeDiccionariosDesdeFicheros(String carpetaConFicherosDeDiccionarios) {
        this.carpetaConFicherosDeDiccionarios = carpetaConFicherosDeDiccionarios;
    }

    // El ServiceLoader solo sabe instanciar con un constructor sin argumentos o con este método;
    // así el constructor puede seguir pidiendo la carpeta, que es lo que usan las pruebas.
    public static SuministradorDeDiccionarios provider() {
        String carpeta = System.getenv(VARIABLE_DE_ENTORNO_CARPETA);
        return new SuministradorDeDiccionariosDesdeFicheros(carpeta != null ? carpeta : CARPETA_EN_CLASSPATH_POR_DEFECTO);
    }

    @Override
    public List<String> getIdiomas() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }









    @Override
    public boolean tienesDiccionarioDe(@NonNull String idioma) {
        // 1. Comprobar si el diccionario está en la cache.
        if(cacheDeDiccionarios.containsKey(idioma)) {
            return true;
        }
        // 2. Si no está en cach, comprobar si existe el fichero correspondiente en la carpeta.
        
    }

    @Override
    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        if(!tienesDiccionarioDe(idioma)) {
            return Optional.empty();
        }
        // Si no está en cache, lo subo a cache.
        if(!cacheDeDiccionarios.containsKey(idioma)) {
            // Lo pongo en cache.... cargándolo del archivo.
        }
        
        // Siempre devuelvo desde cache
        return cacheDeDiccionarios.get(idioma);
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
