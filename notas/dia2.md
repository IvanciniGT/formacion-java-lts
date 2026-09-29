```java

package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;  // Lo que hace es en tiempo de compilacion inyecta código (en este caso un if y un throw new NullPointerException() si el valor es nulo)
                        // Pero la gracia no es solo ahorrar código.. es que en la firma del método quede claro el comportamiento)
                        // Esto tiene que ver con el principio de sustitución de Liskov (SOLID)


public interface Diccionario {

    String getIdioma();

    boolean existe(@NonNull String palabra);

    Optional<List<Significado>> getSignificados(@NonNull String palabra); // Si me pasas nulo, te lanzo un null pointer exception. no lo soporto el valor nulo.

}

public interface SuministradorDeDiccionarios {

    List<String> getIdiomas();

    boolean tienesDiccionarioDe(@NonNull String idioma);            // Quizás una implementación de esta inferfaz, tenga los idiomas (no los diccionarios) precargados.

    Optional<Diccionario> getDiccionario(@NonNull String idioma); // Devuelve el diccionario correspondiente al idioma solicitado, si existe.

}

public interface Significado {

    String getTexto();

    List<String> getEjemplos();

}

// Pruebas! Hemos definido tests? Claro que puedo... y DEBO!
// Pruebas de caja blanca vs pruebas de caja negra?

```

Ayer dijimos que un significado solo era un texto. Vamos a complicarlo un poco. Un significado es un texto y puede tener ejemplos.

Imaginad que estuvieramos haciendo un sistema que no permitiera solo consultar diccionarios/palabras/significados, sino también editarlos.

Completaríamos la interfaz con?

```java
public interface Significado {

    String getTexto();

    List<String> getEjemplos();

    void setTexto(@NonNull String texto);

    void setEjemplos(@NonNull List<String> ejemplos);
}
```

El problema es que aqui estaríamos violando otro de los principio SOLID: Principio de Responsabilidad Unica.
Este principio se entiende muy mal...
No significa que en una clase debamos meter solo conceptos relacionados entre si... Eso lo da otro concepto: COHESION!
El SRP (Principio de Responsabilidad Unica) establece que una clase debe sufrir cambios debidos únicamente a un actor(persona o pto de una organización que puede solicitar cambios). Dicho de otra forma... que solo pueden tomar decisiones sobre su contenido un único tipo de perfil de usuario.

Y en esa tenemos 2 perfiles de usuario muy diferentes.
- Consulta diccionarios
- Edita diccionarios

Si lo hago por separado -> El sistema será más fácil de mantener y evolucionar.
Si lo junto, el sistema puede tener más problemas a la hora de evolucionar y mantener, porque cualquier cambio solicitado por un tipo de usuario podría afectar a los demás.


# Pruebas de nuestro API (caja de caja negra)

Para definir una prueba siempre definimos 3 cosas:
- Contexto
- Acción
- Resultado esperado

> Contexto
 Dado que tengo un suministrador de diccionarios
 Y que ese suministardor de diccionarios tiene diccionario para idioma Español
> Acción
 Cuando le pregunto al suministrador si tiene diccionario para el idioma Español
> Resultado esperado
 Entonces debería devolver true


 Da igual que la implementación de este API trabaje contra una BBDD, contra ficheros, contra un webservice o cualquier otra fuente de datos, el comportamiento esperado debe ser el mismo.

Imaginad la siguiente:
> Contexto
 Dado que tengo un suministrador de diccionarios
 Y que ese suministardor de diccionarios tiene diccionario para idioma Español
 Y que en ese diccionario parece la palabra melón cn 2 significados:
    - Fruta comestible de piel gruesa y pulpa jugosa.
    - Persona con pocas luces. Ej: "Eres un melón"
> Acción
 Cuando le pregunto pido al diccionario de ES entregado por el suministrador por los significados de la palabra "melón"
> Resultado esperado
 Entonces debería devolver los 2 significados de la palabra "melón", a saber:
    - Fruta comestible de piel gruesa y pulpa jugosa.
    - Persona con pocas luces. Ej: "Eres un melón"

Sería suficiente? Puede ser que si, puede ser que no.
Imaginad una implementación de la librería que cada vez que pregunto por una palabra, la busque en BBDD y la devuelva. En este caso, la prueba estaría bien y sería suficiente.

Pero.. Imaginad ahora una implementación donde la librería cachea los resultados de las palabras ya consultadas y solo va a la BBDD si no tiene el resultado en caché.

Esa prueba que hemos definido, prueba la cache? NO
La caché es un detalle de implementación, que si conozco puedo hacerle una preba ad-hc. Esa prueba eso si, servirá solo para la implamentación de cache... si tengo una implemntación sin cache, la prueba es inutil. PRUEBA DE CAJA BLANCA... Necesito saber de la implementación para hacerla.


> Contexto
 Dado que tengo un suministrador de diccionarios
 Y que ese suministardor de diccionarios tiene diccionario para idioma Español
 Y que en ese diccionario parece la palabra melón con 2 significados:
    - Fruta comestible de piel gruesa y pulpa jugosa.
    - Persona con pocas luces. Ej: "Eres un melón"
 Y que le pregunto al diccionario de ES entregado por el suministrador por los significados de la palabra "melón"
> Acción
 Cuando le vuelvo a preguntar al diccionario de ES entregado por el suministrador por los significados de la palabra "melón"
> Resultado esperado
 Entonces debería devolver los 2 significados de la palabra "melón", a saber:
    - Fruta comestible de piel gruesa y pulpa jugosa.
    - Persona con pocas luces. Ej: "Eres un melón"

La primera prueba prueba el camino a BBDD
La segunda prueba prueba el camino de la caché.


Evidentemente una cosa es definir las pruebas y otra ejecutarlas!
Definirlas puedo, ejecuatrlas no.... no tengo contra quién, no hay todavía ninguna implementaciuón.
Las pruebas que defina a nivel del api, las ejecutaré con las futuras implementaciones que haga!


Supuesto que ya hemos definido las pruebas.... nos toca seguir trabajando. Qué podemos hacer ahora? Por dónde seguimos?
Y ahora tenemos 2 caminos posibles:
- Montar una app que consuma este api
- Hacer una implementación del api contra ficheros por ejemplo. o BBDD.

De hecho, gracias a tener el api cerrada, podría tener a 2 equipos trabajando en paralelo en ello. Uno desarrollando la aplicación que consume el API y otro desarrollando la implementación del API contra ficheros o BBDD.

Tiene que ver algo una con la otra? NO.. las monto como proyectos independientes.


## Implementación de nuestro api de diccionarios a ficheros:


```java

package com.curso.diccionario.impl.ficheros;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.Significado;
public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {

    private final String carpetaConFicherosDeDiccionarios; // Carpeta donde se encuentran los ficheros de diccionarios que la sacamos de un properties.

    public SuministradorDeDiccionariosDesdeFicheros(String carpetaConFicherosDeDiccionarios) {
    }

    public List<String> getIdiomas() {
        // Implementación guay
    }

    public boolean tienesDiccionarioDe(@NonNull String idioma){
        // Implementación guay
    }

    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        // Implementación guay
        // Por aquí en algún sitio, devolveré: new DiccionarioDesdeFichero(idioma, rutaAlFichero);
        // Pero lo devuelvo com un "Diccionario". la gente no tiene porque saber con que lo estoy implementando. NO LE IMPORTA A NADIE.
        // NAdie tiene que conocer la existencia de esta clase. NO APORTA VALOR... SOLO CONFUSION! Cuantas menos clases conozcan de mi mejor!
        // Más fácil será usar mi implemenatción.
    }

}

// public: puede ver la clase y usarla todo el mundo
// private: solo clases internasa otra clase... para que solo la clase de fuera pueda usarla
// protected: solo puedan usar esa clase desde el mismo paquete o desde subclases.
// default (sin modificador): solo puedan usar esa clase desde el mismo paquete.
// En java 1.1 había un modificador llamado private protected, eso se quitó... 
// Yo quiero que estas clases sean públicas... pero solo para la implementación que estoy haciendo. No quiero que nadie más las use directamente.
// En C# por ejemplo, podríamos usar el modificador "internal" para indicar que una clase es pública solo dentro del ensamblado (.exe, .dll)
//      Esto en java no existe. Que una clase sea pública dentro de un ensamblado.

public class DiccionarioDesdeFichero implements Diccionario {

    private final String ficheroDeDiccionario;
    private final String idioma; 

    public DiccionarioDesdeFichero(String idioma, String ficheroDeDiccionario) {
        this.idioma = idioma;
        this.ficheroDeDiccionario = ficheroDeDiccionario;
    }

    public String getIdioma() {
        return this.idioma;
    }

    public boolean existe(@NonNull String palabra) {
        // Implementación guay
    }

    public Optional<List<Significado>> getSignificados(@NonNull String palabra) {
        // Implementación guay
    }
}


public class SignificadoDesdeFichero implements Significado {    // POJO = Plain Old Java Object = Clase cutre que solo tiene atributos y getters/setters. Si lógica
                                                                 // Es una clase que sirve para transporte de datos
                                                                 // Tiene algo especial: 
                                                                 // tiene setters? NO
                                                                 // como son sus props: final
                                                                 // Esta clase define objetos INMUTABLES!
                                                                 // Puede cambiarse un SignificadoDesdeFichero una vez creado? NO
                                                                 // Que os parece la sintaxis de java? Cuántas lineas de código hemos escrito para esta clase? 10-12?
                                                                 // Y tiene 2 atributos... anda que si tuviera 10? o 20? flipas!

    private final String texto;
    private final List<String> ejemplos;

    public SignificadoDesdeFichero(String texto, List<String> ejemplos) {
        this.texto = texto;
        this.ejemplos = ejemplos;
    }

    public String getTexto(){
        return this.texto;
    }

    public List<String> getEjemplos(){
        return this.ejemplos;
    }

}

```

Para evitarnos el escribir tanto código en versiones recientes de JAVA, podemos usar los registros (records), que nos permiten definir clases inmutables de manera mucho más concis. Los records se añadieron en Java 14.... la versión LTS que los incluye como funcionalidad final es Java 17.

```java
public record SignificadoDesdeFichero(String texto, List<String> ejemplos) implements Significado {} // 1 línea de código. Esto está más pensado!
// Esto ya me genera props privadas y final, constructor con asignaciones, getters, equals, hashCode y toString de manera automática.

// el problema de los records es que tienen una sintaxis un poco rara al usarlos:

SignificadoDesdeFichero significado = new SignificadoDesdeFichero("Texto de ejemplo", List.of("Ejemplo 1", "Ejemplo 2"));
System.out.println(significado.texto());    // EINS??? y el get() aquí no ! Y queda raro!
System.out.println(significado.ejemplos());

```

La realidad... no se usan tanto... noo porque sean inutiles.. son muy útiles.... solo llegaron tarde al lenguaje. Para cuando llegaron, ya teníamos una forma guay (más guay!) de crear estas cosas... este tipo de clases inmutables: LOMBOK. Además con una sintaxis más familiar.

```java
import lombok.Value;

@Value
public class SignificadoDesdeFichero implements Significado {
    String texto;
    List<String> ejemplos;
}
// y cuando lo uso tengo una sintaxis más natura (en el mundo java)
SignificadoDesdeFichero significado = new SignificadoDesdeFichero("Texto de ejemplo", List.of("Ejemplo 1", "Ejemplo 2"));
System.out.println(significado.getTexto());
System.out.println(significado.getEjemplos());
```

El @Value de lombok también genera clases inmutables al compilar... pero mantiene una sintaxis más familiar para los desarrolladores de Java, con getters tradicionales y un constructor público.








```java
package com.curso.diccionario.app.consola;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros; // Y ACABO DE MATAR EL PROYECTO
// Acabamos de cagarnos en otro de los principios SOLID: Dependency Inversion Principle (DIP)
// Estamos montando un MONOLITO!
// Componentes muy acoplados... rigidamente acoplados.
// Esto es lo que mata la mantenibilidad de un sistema
// A lo largo de la historia, han ido apareciendo muchos PATRONES DE DISEÑO, para ayudarnos a respetar el principio de inversión de la dependencia:
// - Patrón Factoria!
/*
public class FactoriaDeSuministradores {

    public static SuministradorDeDiccionarios crearSuministrador() {
        return new SuministradorDeDiccionariosDesdeFicheros();
    }

}
*/
// - Patrón Singleton (como una factoria, pero más chulito, con garantías de que solo existe una instancia)
/*
public class SingletonDeSuministrador {

    private static final volatile SuministradorDeDiccionarios instanciaSingleton;
    // En un entorno con muchos hilos, donde puede haber concurrencia, este es el patrón de un singleton.
    // el problema de trabajar con hilos, es que los hilos son ejecutados en un core.
    // Y si tengo una máquina multicore, los cres FISICOS tienen CACHE!
    // Y java por defecto guardaría la variable en la cache del CORE al entrar el hilo en ejecución.
    // Y un hilo podría entrar en el if pensando que instanciaSingleton es nula, cuando en realidad otro hilo ya la ha creado, pero el dato aun está en la cache del core.
    // con volatile le decimos a java que para esa variable no use la cache del core, sino que siempre lea el valor actualizado de la memoria principal.
    private final SuministradorDeDiccionarios suministrador;

    private SingletonDeSuministrador() {
        suministrador = new SuministradorDeDiccionariosDesdeFicheros();
    }

    public SuministradorDeDiccionarios obtenerSuministrador() {
        return suministrador;
    }

    public static SingletonDeSuministrador getInstancia() {
        if (instanciaSingleton == null) {                                 // Una vez que se ha creado la instancia, evitar el sincronized, 
                                                                          // que es caro computacionalmente (Solo permite la ejecución de un hilo a la vez)
            synchronized (SingletonDeSuministrador.class) {               // Esto es un semáforo para evitar lo que llamamos un race condition
                                                                          // Esto tiene sentido en entornos multi-hilo, donde 2 hilos simultaneos podrían entrar 
                                                                          // al if a la vez, y en ese momento del tiempo instanciaSingleton podría ser nula...
                                                                          // Y ya dentro del if, los 2 hilos crear la instancia.
                if (instanciaSingleton == null) {                         // Para asegurar que solo creo una instancia de SingletonDeSuministrador
                    instanciaSingleton = new SingletonDeSuministrador();
                }
            }
        }
        return instanciaSingleton;
    }
}
// Similar al patrón factory, pero asegurando que solo se genera una única instancia de la clase.
*/
// - SPI: Service Provider Interface, es un patrón que permite a los desarrolladores implementar y registrar servicios que pueden ser descubiertos y utilizados en tiempo de ejecución. En java lo ofrece la clase java.util.ServiceLoader (esto realmente es de java 1.... pero el uso chulo y su máximo esplendor llega con java 1.9 y los módulos.

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.app.consola.FactoriaDeSuministradores;
import java.util.List;
import java.util.Optional;
public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra) {
        //SuministradorDeDiccionarios suministrador = new SuministradorDeDiccionariosDesdeFicheros();
        //SuministradorDeDiccionarios suministrador = FactoriaDeSuministradores.crearSuministrador();
        // SuministradorDeDiccionarios suministrador = SingletonDeSuministrador.getInstancia().getSuministrador();
        // Esto es una mejora.. sobre todo ahora tengo un sitio UNICO en el código: FactoriaDeSuministradores
        // que decide que implementación se usa...
        // Aunque en 10 clases necesite un suministrador, solo tengo que cambiar la factoria. Esta mejor!
        // De hecho es una clase que nace con esa única RESPONSABILIDAD: decidir qué implementación de SuministradorDeDiccionarios se usa.
        // eso si, me obliga a crear una factoria para cada cosa de la que quiera abstraer la creación.
        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionario(idioma);
        if (potencialDiccionario.isPresent()) {
            Diccionario diccionario = potencialDiccionario.get();
            Optional<List<Significado>> potencialesSignificados = diccionario.getSignificados(palabra);
            if (potencialesSignificados.isPresent()) {
                System.out.println("Se ha encontrado la palabra " + palabra + " en el diccionario del idioma: " + idioma);
                List<Significado> significados = potencialesSignificados.get();
                System.out.println("Significados encontrados: " );
                for (Significado significado : significados) {
                    System.out.println("- " + significado.texto());
                }
            } else {
                System.out.println("No se encontraron significados para la palabra: " + palabra + " en el diccionario del idioma: " + idioma);
            }
        } else {
            System.out.println("No se encontró el diccionario para el idioma: " + idioma);
        }
    }

}

```

# Principio de inversiónd e la dependencia:

Un componente de alto nivel no debería depender de implementaciones de componentes de bajo nivel. Ambos deberían depender de abstracciones.
A nivel de una clase, una depoendencia es un IMPORT!





# Problema del código actual:

Tenemos: 
com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros
com.curso.diccionario.impl.ficheros.DiccionarioDesdeFichero
com.curso.diccionario.impl.ficheros.SignificadoDesdeFichero

Quiero yo que alguien pueda crear instancias de un DiccionarioDesdeFichero? Alguien ajeno a mi SuministradorDeDiccionariosDesdeFicheros debería poder hacerlo?

```java
    new DiccionarioDesdeFichero(idioma, rutaAlFichero);
```

Quiero permitir eso? NO, para qué? Para qué alguien que no sea el SuministradorDeDiccionariosDesdeFicheros debería crear instancias de DiccionarioDesdeFichero?
O para qué alguien que no sea el DiccionarioDesdeFichero debería crear instancias de SignificadoDesdeFichero?

Alguien debería conocer si quiera la existencia de la clase DiccionarioDesdeFichero o SignificadoDesdeFichero? Probablemente no.



Pero voy a más!
Quiero yo que la gente pueda estar creando instancias del SuministradorDeDiccionariosDesdeFicheros? TAMPOCO! Si acaso la factoria o el singleton que he definido. Nadie más!
---


# Modulos.

```java
module MiModulo{
    requires java.base; // ejemplo de un módulo que requiere el módulo base de Java
    exports mi.paquete; // ejemplo de exportación de un paquete del módulo
}
```

Pero el concepto de módulo no es de java. En maven existe el concepto "module". En git existe el concepto de "submodule".
Lo mismo en distintos sitios... 

Un módulo es un conjunto de paquetes. Hasta java 1.8, tenñiamos paquetes, que dentro podían tener clases, interfaces, enumeraciones, anotaciones y subpaquetes.
En java 9 aparece un nivel de agrupación superior llamado módulo, que permite agrupar varios paquetes bajo una misma unidad de modularidad.

Un módulo puede exportar paquetes para que otros módulos puedan acceder a ellos. Solo las clases públicas de un paquete exportado por un módulo pueden ser utilizadas por otros módulos.
Además, un módulo debe declarar los módulos de los que depende (los que necesita para funcionar correctamente).
Un módulo puede ofrecer implementaciones de interfaces, para que sean descubiertos en tiempo de ejecución por el ServiceLoader.
Y un módulo puede indicar al ServiceLoader que requiere de una implementación de un interfaz concreto para funcionar.

Estos son los módulos de java 9. Un modulo se define en un fichero: module-info.java

Nuestro proyecto tiene ya varios módulos:

## Módulo api diccionarios

```java
// module-info.java del módulo api diccionarios
module api.diccionarios {
    exports com.curso.diccionario.api;
    // dentro del paquete hay 3 clases públicas: Diccionario, Significado, SuministradorDeDiccionarios
}
```

## Módulo impl diccionarios en ficheros:
```java
// module-info.java del módulo impl diccionarios en ficheros
module impl.diccionarios.en.ficheros {
    requires api.diccionarios;
    provides com.curso.diccionario.api.SuministradorDeDiccionarios with com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros;
    // Y este módulo NO EXPORTA NADA!
    //. Es decir, NADIE salvo el service loader podrá crear instancias de las clases publicas de este módulo.
}
```

## Módulo app

```java
// module-info.java del módulo app
module app {
    requires api.diccionarios;
    uses com.curso.diccionario.api.SuministradorDeDiccionarios; // Necesita de alguien que provea una implementación de esta interfaz
}
```

En tiempo de ejecución, se escanea el classpath en automático,y se descubren en automático las implementaciones de las interfaces que los módulos declaran mediante `provides ... with ...`.

Cómo queda nuestra class ProcesadorDePeticiones?

```java
import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra) {
        // Esto es un patrón llamado SPI (Service Provider Interface) que permite descubrimiento automático de servicios.
        ServiceLoader<SuministradorDeDiccionarios> loader = ServiceLoader.load(SuministradorDeDiccionarios.class);
        SuministradorDeDiccionarios suministrador = loader.findFirst().orElseThrow(() -> new RuntimeException("No se encontró un suministrador de diccionarios"));
        // Al ejecutar el código, en nuestro classpath tendremos:
        // diccionarios-api-v1.0.0.jar (con las 3 interfaces: Diccionario, Significado, SuministradorDeDiccionarios)
        // diccionarios-impl-ficheros-v1.0.0.jar (con la implementación concreta de SuministradorDeDiccionarios, Diccionario y Significado)
        // y tendré diccioanrio-app-consola-v1.0.0.jar (con la clase ProcesadorDePeticiones y la lógica de la aplicación)
        // Y el service loader en automático buscará en esos jars la implementación de SuministradorDeDiccionarios, y la entrega a quien la pida.
        // Si el día de mañana hago una nueva implementación treabajando con BBDD, lo único que tendría que hacer es incluir el jar correspondiente en el classpath y el service loader la encontrará automáticamente.
        // Quito el jar: diccionarios-impl-ficheros-v1.0.0.jar 
        // Meto el jar:  diccionarios-impl-bbdd-v1.0.0.jar
        // Y no hay que cambiar NI UNA TRISTE LINEA DE CODIGO EN EL PROGRAMA.... solo cambiar un jar por otro.
        // Esto si es guay... y no el patrón factoria, ni el patrón singleton.
        // Esto es lo contrario de un monolito con componenttes aoplados.
        // Estamos montando un monolito con componentes desacoplado: Lo que se llama en la industria un monolito modula = GUAY!
        // Esto facilita la evolución de un sistema.

        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionario(idioma);
        if (potencialDiccionario.isPresent()) {
            Diccionario diccionario = potencialDiccionario.get();
            Optional<List<Significado>> potencialesSignificados = diccionario.getSignificados(palabra);
            if (potencialesSignificados.isPresent()) {
                System.out.println("Se ha encontrado la palabra " + palabra + " en el diccionario del idioma: " + idioma);
                List<Significado> significados = potencialesSignificados.get();
                System.out.println("Significados encontrados: " );
                for (Significado significado : significados) {
                    System.out.println("- " + significado.texto());
                }
            } else {
                System.out.println("No se encontraron significados para la palabra: " + palabra + " en el diccionario del idioma: " + idioma);
            }
        } else {
            System.out.println("No se encontró el diccionario para el idioma: " + idioma);
        }
    }

}

// Llegados a este punto. 2 cosas:
// 1. Esto es lo que vamos a usa en el curso: NOVEDADES JAVA 8-17.
// 2. Realida en la industria...De nuevo JAVA llegó tarde y mal! Igual que con los records...
//    Llevamos muchos años resolviendo este problema de la inversión de dependencias con otro patrón... mucho mejor que el SPI: el patrón de inyección de dependencias (Dependency Injection).
```

## Inyección de dependencias:

Una clase no debe crear instancias de los objetos de los que depende, sino que debe recibirlos desde el exterior.


```java
import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import java.util.List;
import java.util.Optional;

public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra, SuministradorDeDiccionarios suministrador) { // inyección de dependencias
        
        Optional<Diccionario> potencialDiccionario = suministrador.getDiccionario(idioma);
        if (potencialDiccionario.isPresent()) {
            Diccionario diccionario = potencialDiccionario.get();
            Optional<List<Significado>> potencialesSignificados = diccionario.getSignificados(palabra);
            if (potencialesSignificados.isPresent()) {
                System.out.println("Se ha encontrado la palabra " + palabra + " en el diccionario del idioma: " + idioma);
                List<Significado> significados = potencialesSignificados.get();
                System.out.println("Significados encontrados: " );
                for (Significado significado : significados) {
                    System.out.println("- " + significado.texto());
                }
            } else {
                System.out.println("No se encontraron significados para la palabra: " + palabra + " en el diccionario del idioma: " + idioma);
            }
        } else {
            System.out.println("No se encontró el diccionario para el idioma: " + idioma);
        }
    }

}

// Llegados a este punto. 2 cosas:
// 1. Esto es lo que vamos a usa en el curso: NOVEDADES JAVA 8-17.
// 2. Realida en la industria...De nuevo JAVA llegó tarde y mal! Igual que con los records...
//    Llevamos muchos años resolviendo este problema de la inversión de dependencias con otro patrón... mucho mejor que el SPI: el patrón de inyección de dependencias (Dependency Injection).
```
Y hay unframework en java que es el rey en ello: Spring Framework.
Hoy en día, no hay aplicación empresarial JAVA que no se monte sobre Spring Framework.
Lo único que queda en java sin spring es los proyectos legacy.
Ni un proyecto nuevo se monta en java sin usar Spring Framework.

Spring nos regala la inyección de dependencias de manera automática y muy potente, eliminando la necesidad de gestionar manualmente la creación y el suministro de objetos dependientes.


Se usa entonces lo de los módulos... Para algunas cosas... pero no para tantas.

Por ejemplo: JUNIT entero está montado haciendo uso de módulos.

---

# Maven

Maven NO ES UN GESTGOR DE DEPENDENCIAS... al menos NO ES SOLO ESO.

Maven es una herramienta de automatización de tareas en proyectos de software... principalmente JAVA.
Podemos automatizar con maven:
- Gestión de dependencias
- Empaquetado
- Compilación
- Ejecución de pruebas
- Generación de documentación
- Envío de código a sonarqube
- ...

Maven realmente no hace mucho... todo lo hacen plugins.

En un proyecto maven tenemos la siguiente estructura:

proyecto/
    src/
        main/
            java/
            resources/
            ...
        test/
            java/
            resources/
            ...
    pom.xml

# Pom.xml

Es el archivo de configuración de maven para nuestro proyecto.Contiene:
- Datos identificativos del proyecto: Coordenadas: groupId, artifactId, version
- Metadatos: nombre, descripción, url, licencias, desarrolladores, etc.
- Propiedades/variables que usan: O yo dentro del propio archivo pom.xml o los plugins.
- Dependencias: las librerías externas que nuestro proyecto necesita para compilar y ejecutarse.
- Build: Configuración de plugin

Aunque no tengamos plugins definidos, maven de serie vienen con unos 10: 
- Compiler Plugin: Para compilar el código fuente.
- Surefire Plugin: Para ejecutar pruebas.
- JAR Plugin: Para empaquetar el proyecto en un archivo JAR.
- Resources Plugin: Para copiar recursos al directorio de salida.
- Clean Plugin: Para limpiar los archivos generados.
- Install Plugin: Para instalar el artefacto en el repositorio local.
- Deploy Plugin: Para desplegar el artefacto en un repositorio remoto.
- Site Plugin: Para generar documentación del proyecto.
- WAR Plugin: Para empaquetar el proyecto en un archivo WAR (si aplica).
- EJB Plugin: Para empaquetar EJBs (si aplica).

Los plugins permiten ejecutar tareas... y uedo pedir a maven que ejecute una tarea usaando un plugin:
$ mvn <plugin>:<tarea>
$ mvn sonar:sonar
$ mvn jacoco:report    # Informes de cobertura de pruebas .
$ mvn exec:java    # Ejecutar una clase Java específica usando el plugin Exec.

Esto es poco habitual, aunque se puede hacer.

Lo normal es pedir a maven que ejecute hasta un PASO concreto del ciclo de vida de un proyecto:
Maven define un cliclo de vida para un proyecto: 
- validate: Comprueba que el proyecto es correcto y toda la información necesaria está disponible.
- compile: Compila el código fuente del proyecto.
- test: Ejecuta las pruebas unitarias.
- package: Empaqueta el proyecto en su formato distribuible, como un JAR o WAR.
- verify: Realiza cualquier verificación adicional del proyecto.
- install: Instala el artefacto en el repositorio local.
- deploy: Despliega el artefacto en un repositorio remoto.
- clean: Limpia los archivos generados por compilaciones anteriores.

Asociadas a esas fases hay tareas que plugins ejecutan

Asociada a la fase compile está la tarea del Compiler Plugin:
- compile: Compila el código fuente del proyecto.

Pero yo puedo cambiar/añadir tareas adicionales a las fases del ciclo de vida mediante la configuración de plugins en el archivo pom.xml.


---

# Qué son los Streams en Java.

Los streams NO SON MAS QUE LA IMPLEMENTACION en JAVA de un modelo de programación GENERICA llamado Map-Reduce.
De hecho lo tuvimo antes en casi cualquier lenguaje de programación que en JAVA... costó un huevo que llegase a JAVA (versión 1.8)

Lo creó Google. Y lo publicó en un paper.

En java hay varias implementaciones... la del api de java se llama Stream API.
Y está en el paquete java.util.stream.

Hay otra que se usa mucho, pero para volumenes muy grandes de datos... es: Apache Spark.

El tema con streams es que se basa en PURA PROGRAMACION FUNCIONAL... De hecho por eso hasta java 1.8 no existía en Java.

La clave es la clase Stream (realmente es una interfaz).

Es una colección de datos, como lo son los Sets, las listas o los maps.

De hecho, cualquier Set, List o Map se puede convertir en un Stream usando el método .stream().

```java
List<String> lista = List.of("a", "b", "c");
Stream<String> stream = lista.stream();
```

Un Stream también se puede convertir en un List, Set, o Map usando los métodos collect() y Collectors.toList(), Collectors.toSet() o Collectors.toMap().

```java
List<String> lista2 = stream.collect(Collectors.toList());
```

Cual es la diferencia con respecto a las colecciones treadicionales (list, set, map)?
Las colecciones tradcionales llevan funciones (métodos) para trabajar con los elementos de la colección uno a uno.
. get(index) para obtener un elemento por su posición.
. add(element) para añadir un elemento.
. remove(index) para eliminar un elemento por su posición.
. size() para obtener el número de elementos.
. contains(element) para comprobar si un elemento está presente.

En la interfaz Stream, los métodos trabajan sobre TODOS los elementos de la colección.

Están pensados para hacer tratamiento de COLECCIONES DE DATOS.

Y tenemos 2 tipos de métodos en los streams (definidos en el modelo de programación map-reduce):
- Map        Son métodos que al aplicarlos sobre una colección que soporta modelo de programnación map reduce (en el caso de java un Stream), devuelven otra colección de datos que soporta el mismo modelo (en java otro Stream)
- Reduce     Son métodos que al aplicarlos sobre una colección que soporta modelo de programación map reduce (en el caso de java un Stream), devuelven algo que no es un Stream.

Hay muchos métodos de tipo map y muchos métodos de tipo reduce.
Algunos ejemplos de métodos de tipo map son: map(), flatMap(), filter(), sorted()
Algunos ejemplos de métodos de tipo reduce son: reduce(), count(), anyMatch(), allMatch().

Un algoritmo map-reduce siempre tiene la misma pinta:

    Resultado r = coleccionDeDatos
                                    .operacionDeTipoMap1()
                                    .operacionDeTipoMap2()
                                    .operacionDeTipoMap3()
                                    .operacionDeTipoMap4()
                                    .operacionDeTipoMap5()
                                    .operacionDeTipoReduce();

Tenemos que ver como aplicamos esto a la lectura de nuestro fichero.