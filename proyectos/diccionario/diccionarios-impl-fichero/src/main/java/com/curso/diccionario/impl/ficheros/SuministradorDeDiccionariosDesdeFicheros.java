package com.curso.diccionario.impl.ficheros;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;

import lombok.NonNull;


// La lectura de los archivos la hacemos en modo LAZY (cuando se pida la primera vez un diccionario de un idioma se lee el archivo)
// Y se cachea en memoria.
// Siguientes peticiones hacen uso de cache!
// Para la cache, que estructura de datos podemos usar?
public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {

    static final String VARIABLE_DE_ENTORNO_CARPETA = "DICCIONARIOS_CARPETA";
    // Sin la variable, los diccionarios se buscan dentro de un jar del classpath, no en disco.
    static final String CARPETA_EN_CLASSPATH_POR_DEFECTO = "diccionarios/";
    // Dentro de un jar no se puede listar una carpeta sin abrir el jar como sistema de ficheros:
    // de momento, los idiomas que se buscan en el classpath van a capón.
    static final List<String> IDIOMAS_EN_CLASSPATH = List.of("ES");
    private static final String EXTENSION = ".txt";

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
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (!Files.isDirectory(carpeta)) {
            // Se filtran para no anunciar un idioma cuyo fichero no esté en ningún jar.
            return IDIOMAS_EN_CLASSPATH.stream().filter(this::tienesDiccionarioDe).toList();
        }
        try (Stream<Path> ficheros = Files.list(carpeta)) {
            return ficheros.map(fichero -> fichero.getFileName().toString())
                    // Un solo punto: es.prueba.txt no es el diccionario de un idioma "ES.PRUEBA".
                    .filter(nombre -> nombre.endsWith(EXTENSION) && nombre.indexOf('.') == nombre.length() - EXTENSION.length())
                    .map(nombre -> nombre.substring(0, nombre.length() - EXTENSION.length()).toUpperCase(Locale.ROOT))
                    .toList();
        } catch (IOException e) {
            System.out.println("No se pudo listar la carpeta de diccionarios " + carpeta + ": " + e);
            return List.of();
        }
    }

    // Esta se borrará!
    @Override
    public boolean tienesDiccionarioDe(@NonNull String idioma) {
        try {
            return tienesDiccionarioDeIdioma(idioma);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean tienesDiccionarioDeIdioma(@NonNull String idioma) throws Exception{
        return cacheDeDiccionarios.containsKey(idioma)  ||  getFicheroParaIdioma(idioma).isPresent();
    }

    @Override
    // Esa se borrará
    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        try {
            return getDiccionarioBuena(idioma);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    @Override
    public Optional<Diccionario> getDiccionarioBuena(@NonNull String idioma) throws Exception {
        if(!tienesDiccionarioDe(idioma)) {
            return Optional.empty();
        }
        // Si no está en cache, lo subo a cache.
        if(!cacheDeDiccionarios.containsKey(idioma)) {
            // Lo pongo en cache.... cargándolo del archivo.
            URL fichero = getFicheroParaIdioma(idioma).get();
            Map<String, List<Significado>> palabraConSignificados = cargarFichero(fichero);
            cacheDeDiccionarios.put(idioma, new DiccionarioDesdeFichero(idioma, palabraConSignificados));
        }
        
        // Siempre devuelvo desde cache
        return Optional.of(cacheDeDiccionarios.get(idioma));
    }

    // URL y no Path: un fichero dentro de un jar no es un Path del disco, pero sí tiene URL.
    private Optional<URL> getFicheroParaIdioma(String idioma) {
        // Locale.ROOT: con el locale turco, "I".toLowerCase() no da "i".
        String nombre = idioma.toLowerCase(Locale.ROOT) + EXTENSION;
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (Files.isDirectory(carpeta)) {
            Path fichero = carpeta.resolve(nombre);
            if (!Files.isRegularFile(fichero)) {
                return Optional.empty();
            }
            try {
                return Optional.of(fichero.toUri().toURL());
            } catch (MalformedURLException e) {
                System.out.println("Ruta de diccionario no válida " + fichero + ": " + e);
                return Optional.empty();
            }
        }
        // El class loader y no esta clase: el jar con los diccionarios puede ser otro módulo.
        // En el module path, ese módulo tiene que hacer "opens diccionarios" para que se vea.
        return Optional.ofNullable(getClass().getClassLoader().getResource(carpetaConFicherosDeDiccionarios + nombre));
    }

    private static Map<String, List<Significado>> cargarFichero(URL fichero) throws IOException {
        List<String> lineas;
        try (InputStream entrada = fichero.openStream()) {
            lineas = new String(entrada.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        }
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
                                        return new SignificadoDesdeFichero(texto, listadoEjemplos);                        // que uso para crear el Objeto SignificadoDesdeFichero
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
