package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface SuministradorDeDiccionarios {

    List<String> getIdiomas(); // Puede generar error

    // Existe aparte de getDiccionario porque una implementación puede saber qué idiomas
    // tiene sin llegar a cargar el diccionario.
    @Deprecated(since = "1.1.0", forRemoval = true)
    boolean tienesDiccionarioDe(@NonNull String idioma); // Puede generar error

    default boolean tienesDiccionarioDeIdioma(@NonNull String idioma) throws Exception{
        return tienesDiccionarioDe(idioma);
    } 

    @Deprecated(since = "1.1.0", forRemoval = true)
    Optional<Diccionario> getDiccionario(@NonNull String idioma); // Puede generar error

    default Optional<Diccionario> getDiccionarioBuena(@NonNull String idioma) throws Exception { // Puede generar error
        return getDiccionario(idioma);
    } // Java 1.8+
    // La gente no entiende para que existen los métodos default... 
    // Piensan que es para meter lógica compartida a todas las implementaciones.
    // Pero para meter logica compartida en implementaciones que existe en java desde la v1.1 se usaban clases abstractas.
    // Esto solo está con un objetivo: Facilitar la evolución de la API sin romper las implementaciones existentes.
    // Poder cambiar la api generando un minor, y no un major.


    // Esto sería lobueno.. y lo que yo haría en un proyecto.
    // en clase vamos a hacer otra cosa... para explicaros un concepto nuevo... QUE VOY A METER CON CALZADOR...
    // Realmente el concepto no aplcia aqui... Aquñi lo suyo es lanzar una exception.

    // Hay veces que una función puede devolver varias cosas DISTINTAS ENTRE SI.
    // O requerir al menos información adicional en alguna de ellas.
    // Y el Optional se queda pobre.
    // El Optional me pemite devolver algo o no!
    // Pero incluso si no lo devuelve, me impide saber porque no lo devuelvo.

    // doLogin(usuario, password) qué devuelve?
    // - Exito                                   |
    // - Fallo: usuario no existe                |
    // - Fallo: password incorrecta               > Esto son caminos posibles... No excepcionales.
    // - Fallo: cuenta bloqueada                 |
    // - Fallo: El sistema está en mantenimiento |
    // - Fallo: error desconocido de sistema        ---> Exception

    // en Typescript puedo hacer que una función devuelva muchas cosas diferentes:
    // ts
    // function doLogin(usuario: string, password: string): LoginExito | LoginFallido | SistemaEnMantenimiento {
    // }

    // en Java no se puede... En java necesito crear una base común a todos esos tipos:

    // interface LoginResultado {}
    // class LoginExito implements LoginResultado {}
    // class LoginFallido implements LoginResultado {}
    // class SistemaEnMantenimiento implements LoginResultado {}

    //java
    // public LoginResultado doLogin(String usuario, String password) {
    //     // implementación
    // }

    // Ahora bien...
    // Yo consumo ahora esa función. Qué espero que devuelva la función? LoginResultado
    // Ya... con que valores? Para que los trate en mi pantalla y muestre mensajes distintos: 
    //    LoginExito, LoginFallido, SistemaEnMantenimiento
    // Bien... Y si el día de mañana cambian el backend y crean:
    //    public interface CuentaBloqueada implements LoginResultado {}
    // Espero yo que estoy conumiendo el API ese valor nuevo: CuentaBloqueada? Ni de coña
    // Tengo un if para ese valor nuevo? Ni de coña.
    // Resultado: El sistema falla!

    // Y esto ha apsado por no respetar el princio de sustución de Liskov (Liskov Substitution Principle)
    // Que me dice que CIERRE EL PUÑETERO CONTRATO!

    // El problema gordo es COMO LECHES ME ENTERO YO DE QUE HAN METIDO UN NUEVO TIPO DE RESULTADO QUE NO ESPERABA.
    // CuentaBloqueada, por ejemplo, y yo ni me entero.

    // Y aparecen en JAVA los sealed classes (clases selladas) para resolver este problema:

    // public sealed interface LoginResultado permits LoginExito, LoginFallido, SistemaEnMantenimiento {}
    
    // Si alguien intentase crear una nueva clase que implemente LoginResultado sin declararla en el permits, el compilador lanzaría un error.
    // Nadie puede crear un CuentaBloqueada sin declararla en el permits.

    // Y si lo meten en el permits, entonces el compilador me dará error a mi al consumir:
    // switch (resultado) {  // Nuevo switch de Java 17, que permite pattern matching con sealed classes
    //     case LoginExito exito -> ...
    //     case LoginFallido fallido -> ...
    //     case SistemaEnMantenimiento mantenimiento -> ...
    // }

    // Y si meten uno nuevo, me da error de compilación = GUAY! Ya me he enterado... ya no explota mi sistema en silencio.
}
