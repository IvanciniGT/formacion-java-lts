package com.curso.diccionario.impl.ficheros;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.impl.ficheros.formato.CatalogoDeDiccionariosEnClasspath;
import com.curso.diccionario.impl.ficheros.formato.FormatoDeFicheroDeDiccionario;

import lombok.NonNull;


// La lectura de los archivos la hacemos en modo LAZY (cuando se pida la primera vez un diccionario de un idioma se lee el archivo)
// Y se cachea en memoria.
// Siguientes peticiones hacen uso de cache!
// Para la cache, que estructura de datos podemos usar?
public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {

    static final String VARIABLE_DE_ENTORNO_CARPETA = "DICCIONARIOS_CARPETA";
    // Sin la variable, los diccionarios se buscan dentro de los jars del classpath, no en disco:
    // los de los proyectos diccionario-es, diccionario-en...
    static final String CARPETA_EN_CLASSPATH_POR_DEFECTO = CatalogoDeDiccionariosEnClasspath.CARPETA_POR_DEFECTO;
    private static final String EXTENSION = ".txt";

    // System.Logger: viene en el JDK (java.base), así que no añade ninguna dependencia.
    private static final System.Logger log = System.getLogger(SuministradorDeDiccionariosDesdeFicheros.class.getName());

    private final String carpetaConFicherosDeDiccionarios;
    private final CatalogoDeDiccionariosEnClasspath catalogo;

    private final Map<String, Diccionario> cacheDeDiccionarios = new ConcurrentHashMap<>();
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
    //
    // Versión 1.2.0: ConcurrentHashMap y no WeakHashMap, por dos motivos.
    //  - WeakHashMap no es seguro entre hilos, y esta implementación ahora la usa un servidor
    //    que atiende peticiones en paralelo: dos hilos escribiendo a la vez pueden corromper el mapa.
    //  - Y tampoco liberaba nada: el valor (DiccionarioDesdeFichero) guarda en su campo idioma el
    //    mismo String que es la clave, así que el valor mantenía viva su propia clave.
    // Con pocos idiomas, y cada uno de unos pocos MB, no merece la pena una cache con expulsión.

    public SuministradorDeDiccionariosDesdeFicheros(String carpetaConFicherosDeDiccionarios) {
        this.carpetaConFicherosDeDiccionarios = carpetaConFicherosDeDiccionarios;
        this.catalogo = new CatalogoDeDiccionariosEnClasspath(getClass().getClassLoader(), carpetaConFicherosDeDiccionarios);
    }

    // El ServiceLoader solo sabe instanciar con un constructor sin argumentos o con este método;
    // así el constructor puede seguir pidiendo la carpeta, que es lo que usan las pruebas.
    public static SuministradorDeDiccionarios provider() {
        String carpeta = System.getenv(VARIABLE_DE_ENTORNO_CARPETA);
        return new SuministradorDeDiccionariosDesdeFicheros(carpeta != null ? carpeta : CARPETA_EN_CLASSPATH_POR_DEFECTO);
    }

    // Esta se borrará!
    @Override
    public List<String> getIdiomas() {
        try {
            return getIdiomasDisponibles();
        } catch (Exception e) {
            log.log(Level.WARNING, "No se pudieron averiguar los idiomas disponibles", e);
            return List.of();
        }
    }

    @Override
    public List<String> getIdiomasDisponibles() throws IOException {
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (!Files.isDirectory(carpeta)) {
            return catalogo.idiomas();
        }
        try (Stream<Path> ficheros = Files.list(carpeta)) {
            return ficheros.map(fichero -> fichero.getFileName().toString())
                    // Un solo punto: es.prueba.txt no es el diccionario de un idioma "ES.PRUEBA".
                    .filter(nombre -> nombre.endsWith(EXTENSION) && nombre.indexOf('.') == nombre.length() - EXTENSION.length())
                    .map(nombre -> nombre.substring(0, nombre.length() - EXTENSION.length()).toUpperCase(Locale.ROOT))
                    .toList();
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
            log.log(Level.WARNING, "No se pudo cargar el diccionario de " + idioma, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Diccionario> getDiccionarioBuena(@NonNull String idioma) throws Exception {
        // Siempre devuelvo desde cache
        Diccionario enCache = cacheDeDiccionarios.get(idioma);
        if (enCache != null) {
            return Optional.of(enCache);
        }
        Optional<URL> fichero = getFicheroParaIdioma(idioma);
        if (fichero.isEmpty()) {
            return Optional.empty();
        }
        // computeIfAbsent y no containsKey + put: con dos peticiones a la vez del mismo idioma,
        // el fichero se lee una sola vez y las dos reciben el mismo diccionario.
        try {
            return Optional.of(cacheDeDiccionarios.computeIfAbsent(idioma, clave -> {
                try {
                    return new DiccionarioDesdeFichero(clave, FormatoDeFicheroDeDiccionario.leer(fichero.get()));
                } catch (IOException e) {
                    // La función de computeIfAbsent no puede lanzar comprobadas: se envuelve y se desenvuelve abajo.
                    throw new UncheckedIOException(e);
                }
            }));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    // URL y no Path: un fichero dentro de un jar no es un Path del disco, pero sí tiene URL.
    private Optional<URL> getFicheroParaIdioma(String idioma) {
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (!Files.isDirectory(carpeta)) {
            return catalogo.fichero(idioma);
        }
        // Locale.ROOT: con el locale turco, "I".toLowerCase() no da "i".
        Path fichero = carpeta.resolve(idioma.toLowerCase(Locale.ROOT) + EXTENSION);
        if (!Files.isRegularFile(fichero)) {
            return Optional.empty();
        }
        try {
            return Optional.of(fichero.toUri().toURL());
        } catch (MalformedURLException e) {
            log.log(Level.WARNING, "Ruta de diccionario no válida " + fichero, e);
            return Optional.empty();
        }
    }

}
