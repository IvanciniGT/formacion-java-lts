
# Día 1

## JAVA LTS (Long Term Support)

- Java 1.8 (LTS) - 2014
- Java 9 - 2017
- Java 11 (LTS) - 2018
- Java 15 - 2020
- Java 17 (LTS) - 2021

## Repaso de conceptos básicos de JAVA

Con JAVA nos referimos a 2 cosas diferentes:
- Lenguaje de programación
- Máquina virtual (JVM) dodne ejecutamos código escrito en lenguaje de programación `byte-code`

## JAVA como lenguaje de programación

### Tipado dinámico/débil vs Tipado estático/fuerte

JAVA es tipado estático o dinámico? Java es tipado estático.

A qué nos referimos con tipado estático o dinámico?

Todo lenguaje de programación, nos permite crear programs que manejan datos.
Y en todo lenguaje de programación tenemos tipos de datos.

En java tenemos tipos de datos?             int, double, boolean, char, String, List<?>
En python tenemos tipos de datos?           int, float, bool, str, list, dict, tuple
En javascript tenemos tipos de datos?       number, string, boolean, object, array, null, undefined, symbol, bigint

Entonces, qué diferencia un lenguaje de tipado dinámico de uno de tipado estático?

```python
# Python es tipado dinámico
texto = "hola"
```

```js
// JavaScript es tipado dinámico
var texto = "hola";
```

```java
// Java es tipado estático
String texto = "hola";
            // <-  LA FLECHA NO VA ASI! No es el dato "hola" el que se asigna a la variable texto
            // ->  La flecha va así!    Es la variable `texto` la que se asigna al valor `"hola"`
```

> Qué es una variable?

El problema principal qeu tenemos es que el concepto de variable cambia de lenguaje en lenguaje.
Es más, en ocasiones, el concepto de variable cambia (Como en JAVA, que hay varias definiciones válidas de variable, distinats entre si)

Muchas veces pensamos en una variable como en un "contenedor" donde guardamos DATOS!... Y Eso no está mal... siempre y cuando estemos en lenguaje C, C++, Fortran... 
Pero de hecho en JAVA, JS, TS, Python (y acabo de poner ls 4 lenguajes de programación más usados del mundo), una variable es otra cosa.

Una variable es una referencia a un dato que tenemos guardado en RAM.

> Qué hace esa linea de código?

1. "hola"               Crea un dato de tipo String en memoria, con valor "hola". 
                        Pregunta: Por qué un dato de tipo String y no otro? 
                        Por las comillas.. así se escriben en JAVA, JS, TS, Python los strings.
                        Tiene que ver algo con que al principio de la linea ponga String? NADA!
                            De hecho la linea en JAVA podría haber sido :     `Object texto = "hola";``` 
                            y seguiría habiendo creado un objeto de tipo String.
                        Entonces el `String` qué es? El tipo de datos de la VARIABLE.
                        Ah ostias... es que resulta que las variables TAMBIEN TIENEN TIPOS DE DATOS.

                        Pensad en la memoria RAM como si fuera un cuaderno de cuadrícula. De hecho es lo que es literalmente.                        
                        Con ese trozo de código "hola" lo que hemos pedido a la JVM es que cree un dato de tipo String en memoria RAM.. el algún sitio (npi de donde en el caso de JAVA). Hemos abierto el cuaderno por algún sitio, y hemos escrito "hola" en una de las celdas.
He dicho antes:
Todo lenguaje de programación permite crear programas que manejan datos.. Y todo lenguaje de programación tiene distintos tipos de datos.
PERO EN ALGUNOS LENGUAJES DE PROGRAMACION, las VARIABLES TAMBIEN TIENEN TIPOS DE DATOS, como es el caso de JAVA, C, C++, TS.

En estos lenguajes, llamados de tipado estático/fuerte, una variable solo puede apuntar/almacenar (dependiendo del lenguaje) a datos del tipo que se le ha declarado a la variable (o un subtipo).
2. String texto        Crea una variable llamada "texto" que solo puede apuntar a datos de tipo String o un tipo subtipo de String.
                           Si la RAM es como un cuaderno, una variable en JAVA es como un postit. 
                           En nuestro caso hemos arrancado unn postit de los verdes (String)... luego hay postits amarillos (por ejemplo de tipo Integer), rosas (por ejemplo de tipo Float), etc.
                           En el postit hemos escrito "texto" NO "hola".
3. =                       Y pego el postit al lado del dato "hola" en la memoria RAM (cuaderno)
                           Asignar la variable al dato que tengo en RAM.


En el caso de JAVA es un poco más complejo... esto es cierto para tipos complejos (objetos). Para tipos simples (int, float, boolean, etc.) una variable atiende más a la definición de "un cajoncito/contenedor" en la memoria RAM donde se guarda directamente el valor.

En lenguajes como python o js, las variables NO TIENEN TIPO DE DATO... cuidao! hay gente que dice.. es que la variable cambia de tipo de dato. NO... es que la variable NO TIENE TIPO DE DATO... y por ende puedn apuntar a datos de cualquier tipo:

```javascript
var texto = "hola";
texto     = 3;           // en JS esto funciona perfecto, porque la variable "texto" no tiene tipo de dato... y puede apuntar a datos de cualquier tipo.
                         // Y eso no significa que JS no tenga tipos de datos:     "hola" es un string y 3 es un number.
                         // Quién no tiene tipo de datos es la variable
```                     

Pero... en java funcionaría esto?

```java
var texto = "hola"; // Esto es válido en JAVA desde la versión 10. En versión 10 se añade al lenguajhe java la pàlabra reservada "var" al definir una variable.
```

Y esto funciona entonces?
```java
texto     = 3;      // NO. JAVA es un lenguaje de tipado ESTATICO.
                    // La palabra var en java no es igual a la palabra var en JS. 
                    // En java ocurre lo que se llama INFERENCIA DE TIPOS... ocurre en muchos sitios... ya lo iremos viendo.
                    // el tipo de datos de la variable "texto" se infirió al asignarle su primer valor: "hola"...
                    // texto es una variable de tipo String, aunque no lo haya escrito... y haya usado la palabra var. 
                    // El tipo de dato de la variable TEXTO se INFIRIÓ al asignarle su primer valor: "hola".
                    //Si ahora trato de asignarle un valor de un tio de datos distinto a String o a un subtipo compatible, obtendré un error de compilación.
```

Aprovecho para repasar otro tema:

```java
String texto = "hola";
texto = "adios";
```

Qué hace la segunda linea? como se ejecuta?
1. "adios"           Coloca en memoria RAM un String con valor "adios".
                     Dónde? En el mismo sitio donde estaba el "hola" o en otro sitio? En otro sitio distinto
                     LLegados a este punto, en nuestra RAM tenemos 2 objetos de tipo String: "hola" y "adios".
                     Si esto fuera un programa escrito en C, el valor "adios" sobrescribiría directamente el valor "hola" en la misma ubicación de memoria. La variable actuaía como un contenedor, donde almaceno un dato.
                     Pero no estamos en C. Y esto hace... que el mismo programa escrito en C o escrito en JAVA, tenga necesidades de RAM diferentes.
                     Este programa requiere en JAVA el doble de RAM al ejecutarse que si lo hbiera escrito en C.

                     JAVA hace un abuso de la memoria RAM. Y la pregunta es:
                     ¿Esto es bueno o malo? Ni bueno ni malo... Es un FEATURE de JAVA.
                     JAVA se diseño intencionalmente con ese comportamiento.
                     No tenía sentido crear un programa con la sintaxis de JAVA que hiciera un uso eficiente de la memoria... ya existía ese lenguaje: C++
                     La idea era crear un lenguaje nuevo. Un lenguaje que evitase a los desarrolladores tener que lidiacon las sutilezas de la gestión de la memoria RAM.
                     En C, C++ hay que reservar explícitamente la memoria para cada dato que se quiera almacenar, y también hay que liberarla manualmente cuando ya no se necesite.
                     En JAVA me olvido de eso. Eso hace el desarrollo "a priori" más sencillo y menos propenso a memory leak (bugs por no liberar memoria cuando ya no es necesaria, que hacen que el programa crezca en uso de RAM de manera innecesaria).
                     En JAVA tenemos el recolector de basura (Garbage Collector), que se encarga de liberar automáticamente la memoria de los objetos que ya no son accesibles desde el programa... igual que en JS, PYTHON...
                     Más bien, en JVM, en Node.js o CYTHON, el recolector de basura se encarga de gestionar la memoria automáticamente, liberando los objetos que ya no son accesibles y evitando así memory leaks.
2. texto =           Lo que hacemos aquñi es DESPEGAR EL POSTIT DE LA HOJA donde estaba pegado (al lado del valor "hola") y 
                     pegarlo en otro lugar (al lado del valor "adios").
                     LO QUE VARIA (variable) es la UBICACION del POSTIT (la referencia al objeto en memoria).
                     Antes la variable apuntaba al objeto "hola" en memoria. Ahora apunta al objeto "adios".
                     Y el objeto "hola" queda huérfano de variable. No hay variable queapunte a ese objeto... y en java (js, python) ese objeto se convierte en inalcanzable, por lo que el recolector de basura eventualmente liberará la memoria que ocupaba( o no... npi ... dependede si hace falta y cuándo hace falta.)
                     Esto hace que JAVA (JVM) tenga un comportamiento NO DETERMINISTA... e invalida el uso de JVM (y de cualquier lenguyaje que genere byte-code) para cierto tipo de proyectos. 
                     Bueno.. por eso hay muchos lenaguejs de programación y entornos de ejecución diferentes.

                     En JAVA preferimos crear una app menos eficiente en cuanto a gestión de RAM, pero no nos complicamos con la gestión manual de la memoria.

                        App1 hecha en C requiere 200 horas de desarrollador con conocimientos avanzados de gestión de memoria (60€/hora) = 12.000€
                        La misma app1 hecha en JAVA requiere 140 horas de desarrollador con conocimientos básicos de gestión de memoria 50€/hora = 7.000€
                        A cambio, necesito más RAM.

                        Con 5.000 euros de diferencia, que se metan pastillas de RAM al servidor... Los que sean necesarios! Y PUNTO PELOTA!

### Paradigmas de programación

> ¿Qué es un paradigma de programación?

Paradigma de programación e sun nombre "hortera" que los desarrolladores damos a las formas en las que podemos usar un lenguaje para expresarnos.
Ni siquiera es algo propio de los lenguajes de programación.. En los lenguajes naturales tenemos el mismo concepto:

> Felipe, pon una silla debajo de la ventana. IMPERATIVA - Damos una orden.
Lo cierto es que cada vez odiamos más el lenguaje imperativo... es un tostón.
Qué me diría Felipe si no hay hueco para colocar una silla debajo de la ventana? 
- NoLeftSpaceOnDeviceException
- ExitCode 127
- Status Code 400: Bad Request

Y entonces empieza el "cerdeo" tipico de la programación imperativa:

> felipe, IF hay algo que no sea una silla debajo de la ventana: (CONDICIONAL AL CANTO)
>   Quítalo                                                             IMPERATIVA
> felipe (if no hay silla debajo de la ventana):                 (CONDICIONAL AL CANTO)
>    Felipe, IF NOT SILLA (silla == false) then: 
>               GOTO IKEA:
>               Compras silla                                                        IMPERATIVA
> Felipe, pon una silla debajo de la ventana.                           IMPERATIVA - Damos una orden.

Este es el problema del lenguaje imperativo.
Mirad:

> Felipe, debajo de la ventana tiene que haber una silla. Es tu responsabilidad.    ESTO DE HECHO ES PARADIGMA DECLARATIVO
Es imperativo? No... no doy órdenes.
Traslado la responsabilidad de conseguir mi objetivo a Felipe.

En imperativo describo a Felipe LO QUE DEBE HACER para conseguir el estado que quiero.
En declarativo describo EL ESTADO QUE QUIERO CONSEGUIR, sin dar instrucciones paso a paso.
Adoramos el paradigma declarativo... cada día lo usamos más.Java Soporta paradigma declarativo?
Desde hace muchas versiones: 1.5... cuando en JAVA se introdujeron las anotaciones.
Las anotaciones de java son paradigma declarativo.
- JPA (Java Persistence API) @Entity @Table
- Spring Framework @Component @Service @Repository

En el mundo de la programacióin hay ciertos paradigmas que usamos mucho:
- Imperativo                Cuando damos instrucciones que la computadora debe ejecutar secuencialmemnte.
                            En ocasiones necesitamos romper la secuencialidad, y aparecen las típicas estructuras de control de flujo de los lenguajes que  soportan paradigma imperativo: IF, SWITCH, FOR, FOR EACH, WHIL, TRY, CATCH
                            La mayor parte del código que escribimos en JAVA es imperativo.
- Procedural                Cuando el lenguaje me permite agrupar secuencias de instrucciones en un bloque al que pongo un nombre
                            y posteriormente ejecutar ese bloque de instrucciones mediante ese nombre
                            y decimos que el lenguaje soporta el paradigma procedural.
                            A esos bloques dependiendo del lenguaje los llamamos: funciones, procedimiento, método, subrutina....
                            Java por supuesto soporta paradigma procedural

                            Para qué creamos funciones? Dicho de otra forma, que ventajas tiene sobre el paradigna imperativo el paradigma procedural?
                            - Reusar código
                            - Mejorar la estructura / legibilidad del código



- Orientación a Objetos     Todo lenguaje permite manejar datos. Y esos datos pueden ser de distinto tipo... y admitir distintas operaciones.
                            Todo lenaguje viene con una serie de tipos de datos predefinidos:

                                                Caracteriza por                 Admite los comportamientos:
                                String          una secuencia de caracteres     .toUpperCase() .toLowerCase() .charAt(index) .substring(start, end)
                                Fecha           día, mes, año                   .caesEnBisiesto? caesEnJueves?, dameElSiguienteLunes?
                        
                            Hay lenguajes que me permiten definir mis propios tipos de datos, con sus características y comportamientos específicos.

                                Usuario         nombre, email, fechaDeNacimiento   .esMayorDeEdad? .tieneEmailValido? .doLogin()

                            Estos lenguajes decimos que adoptan el paradigma de orientación a objetos.
                            Esos tipos de datos son lo que denominamos clases en el paradigma de orientación a objetos.
                            Luego hay conceptos derivados más avanzados:
                            - Herencia: Permite que una clase derive de otra, reutilizando código y estableciendo relaciones jerárquicas.
                            - Contrato (Interface): Define un conjunto de métodos que una clase debe implementar, permitiendo la abstracción y la separación de responsabilidades.
                            - Sobrecarga: Permite que una clase tenga múltiples métodos con el mismo nombre pero con diferentes parámetros, facilitando la reutilización de nombres y la flexibilidad en la definición de comportamientos.
                            - Sobreescritura (Override): Permite que una subclase proporcione una implementación específica de un método que ya está definido en su superclase, permitiendo la personalización del comportamiento heredado.
                            Hay lenguajes que soportan algunos conceptos... otros otros.
                                - JAVA Soporta herencia, interfaces, sobrecarga y sobreescritura.
                                - PYTHON Soporta herencia múltiple (que java no)
                                - JS no soporta contratos (interfaces) de manera nativa.
                            Por supuesto JAVA Es un lenguaje Orientado a Objetos.. o mejor dicho, que soporta ENTRE OTROS, el poaradigma de orientación a objetos.
- Paradigma funcional:
        Esto, (JAVA 8) junto con los MODULOS y la modularización de la JVM (Proyecto Jigsa, Java 9) son los mayores cambios que ha sufrido JAVA como lenguaje de programación.. y que han afectado a la JVM en su conjunto.
        Y son dos cosas de las que tenemos que hablar muy despacio.
        Todo el API de Java está migrando hacia programación funcional. Adoramos la programación funcional... nos permite escribir código más conciso, expresivo y fácil de razonar, comentar.. pero hay que hacerse a ella. Y a priori no es evidente.

        Es un paradigma muy antiguo... y sufrimos mucho hasta que en la versión 1.8 se incluyó el soporte a programación funcional en Java.
        Definición práctica: 
            Cuando el lenguaje me permite que una VARIABLE apunte a una función y posteriormente ejecutar la función desde la variable decimos que el lenguaje soporta programación funcional.

            El concepto de programación funcional es muy simple.
            Lo complejo es lo que puedo empezar a hacer, una vez que el lenaguje soporta esto.
            Y todo cambia!
            - puedo empezar a definir funciones que acpten funciones como argumentos
            - puedo empezar a definir funciones que devuelvan funciones como resultado (clousures)

            Para qué creamos funciones? Toma una nueva dimensión.
                - Reusar código
                - Mejorar la estructura / legibilidad del código
                - Porque no me quedan más narices!

# Setters y Getters en JAVA.

Para qué sirven? Para poder acceder a datos privados. Bien... y es para lo que los usamos en el 90% de los casos? Para proteger datos privado
Me temo que no?

```java

public class Persona {

    public String nombre;
    public int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

}

// En algún sitio del código....
Persona p = new Persona("Juan", 30);
p.edad=31;
System.out.println(p.edad);
System.out.println(p.nombre);

```

Lo que acabo de hacer compila? PERFECTO!
Y funcionaría perfecto! Sería buena práctica o me despiden al día siguiente? MALA PRACTICA!

```java

public class Persona {

    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }
    public int getEdad() {
        return edad;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }

}

// Con lombok:
@Getter // Quiero tener getters en mis atts privados
@Setter // Quiero tener setters en mis atts privados
@AllArgsConstructor // Quiero tener un constructor con todos los atributos privados
public class Persona {

    private String nombre;
    private int edad;

}
// En algún sitio del código....
Persona p = new Persona("Juan", 30);
p.setEdad(31);
System.out.println(p.getEdad());
System.out.println(p.getNombre());

```
Esto es la buena práctica... y es cierto.. y es cierto que lo otro es muy mala práctica. POR QUE?
Lo de arriba es una mala práctica debido a una carencia enorme del lenguaje JAVA...



```ts
export class Persona {

    constructor(public nombre: string, public edad: number) {}

}
```

---

Veámoslo con perspectiva:

# Día 1

```java

public class Persona {

    public String nombre;
    public int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

}
```
Compila y funciona perfecto!

# Día 2-100
Empiezo yo y mis compañeros a usarla:

```java
// En algún sitio del código....
Persona p = new Persona("Juan", 30);
p.edad=31;
System.out.println(p.edad);
System.out.println(p.nombre);
```

# Día 101

Me planteo ... que una edad no puede ser negativa!
Eso implica un if... y una exception. Donde lo pongo?
NO HAY SITIO PARA ESCRIBIRLO!. Podría escribirlo en constructor.. pero si luego cambian el dato... no aplica el constructor.
Por nrrices necesito meter código en una función. NO HAY OTRO SITIO EN JAVA
Y necesito apsar al siguiente código:

```java
public class Persona {

    public String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.setEdad(edad);
    }

    public void setEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.edad = edad;
    }
    public int getEdad() {
        return edad;
    }
}
```

# Qué tengo el día 102 ?

A tod@s los compis de mi empresa, kalasnikov en mano, buscándome porque su código. NO COMPILA!

```java
// En algún sitio del código....
Persona p = new Persona("Juan", 30);
//p.edad=31;
p.setEdad(31);
//System.out.println(p.edad);
System.out.println(p.getEdad());
System.out.println(p.nombre);
```

Para evitarme esto, en JAVA, me dicen... El día 1, cuando crees la clase, mete ya todas sus propiedades como privadas y proporciona métodos getter y setter para tener un sitio
el día de mañana, por si acaso lo necesitas, donde poder meter código y no jodr al resto de compañeros.

Osea.. que por si acaso el día de mañana quiero meter un cambio, tengo que crear getters y setters para todo? Por si acaso?
QUE LENGUAJE MAS BONITO ES JAVA!

Claro.. En JS, C#, Python... en todo lenguaje de programación del mundo moderno, salvo JAVA, existe el concepto de property, que evita esto:

```csharp
// Dia 1:

public class Persona {

    public String nombre;
    public int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
}

// Dia 2-100
Persona p = new Persona("Juan", 30);
System.out.println(p.edad);
p.edad = 31;

// día 101
public class Persona {

    public String nombre;
    private int edad;
    public property int Edad {
        set { 
            if (value < 0) {
                // lanzo exception:
                throw new IllegalArgumentException("La edad no puede ser negativa");
            }
            edad = value; 
        }
        get { return edad; }
    }
}

// Y no afecta a nadie:
la linea p.edad = 31; // Sigue igual... pero internamente llama a la función set del property Edad.
```

Esto es un lenguaje bien pensad.
El mismo concepto está en python.
El mismo concepto está en JS.
El mismo concepto está en TS.
El mismo concepto está en Kotlin.
En java no.

Bueno.. siempre nos quedará Lombok: @Getter y @Setter.

Pero lo importante a lo que iba es otra cosa.
Muchas veces hacemos mierdas como los setters y getters... o cosas peores con un único objetivo: FACILITAR LA MANTENIBILIDAD Y EVOLUCIÓN DEL SOFTWARE!

Y esta es la clave!

Hacer un programa que funcione NO TIENE MERITO! Se da por descontado.

Para que entendaís el concepto:

Hacer un coche que funcione NO TIENE MERITO! Se da por descontado!
Cuando voy a comprar un coche pregunto si funciona? O lo doy por descontado? Se da por descontado!
De hecho, si no funciona... ni siquiera recibe el nombre de COCHE. Llamame loco! Será una escultura de hierro con ruedas... pero un coche no.
Por definición un coche debe andar!

Y por definición un programa debe funcionar. Si no funciona lo que tienes es un archivo de texto con pretensiones.
La clave, y es lo que diferencia un Senior de un Junio es entender que el objetivo no es que el programa funcione... el objetivo es un programa que sea fácil de mantener y evolucionar en el tiempo.

Aquí sale un concepto, que las empresas tienen cada vez más claro: TCO: Total Cost of Ownership (Costo Total de Propiedad).
Me da igual que el producto/desarrollo inicial sea más caro... Lo importante es que en el medio y largo plazo, el coste de propiedad (mantenimiento, evolución, etc.) sea menor.

Y hay muchas cosas (cambios que se van haciendo al lenguaje) que vcan en este sentido: Tener un software más fácil de mantener y evolucionar en el tiempo!

---
Llegados a este punto.. Voy a presentar el sistema/producto/proyecto que vamos a hacer en el curso, a lo largo del martes, miércoles y jueves.

# Proyecto:

Queremos una aplicación de consola que reciba 2 datos: palabra, idioma y devuelva los significados de esa palabra en ese idioma.

## Pregunta.. por donde empiezo? CASOS DE USO!

### Caso de uso 1: Buscar los siognificados de una palabra que existe en un idioma

    $ buscarPalabra ES melón

La palabra melón existe en el idioma ES y tiene los siguientes significados:
- MelónFruto grande, redondo y de pulpa jugosa y dulce.
- Persona con pocas luces: Eres un melón!

### Caso de uso 2: Buscar los siognificados de una palabra que NO existe en un idioma

    $ buscarPalabra ES archlococo

La palabra archlococo NO existe en el idioma ES.

### Caso de uso 3: Buscar los significados de una palabra en un idioma que NO EXISTE

    $ buscarPalabra ELF melón

Lo siento, pero no tenego diccionario para el idioma ELF.

### Caso de uso 4: Invocar al programa sin los argumentos necesarios

    $ buscarPalabra ES
    $ buscarPalabra

Faltan argumentos.
La forma correcta de invocar el programa es:

    $ buscarPalabra <IDIOMA> <PALABRA>

Ejemplo:

    $ buscarPalabra ES melón

Esos son los 4 casos de uso de nuestro programa.

---

Cuántos proyectos hacemos?
Cuántos repos de git?
Cuántos archivos de maven: pom.xml?

Para este proyecto?
- Con 1 vale          III
- Yo creo que de 10 no bajan....

Desde luego , con 1 proyecto puedo hacer algo que FUNCIONE!
Pero ya hemos dicho que no es el objetivo.... el objetivo es que sea mantenible y evolucionable!

Hay gente que llama a esto SOBREINGENIERIA!
Sobre ingeniería es esto para un script cutre que voy a ejecutar 3 veces y que evolucionará poco.

Este programa va a evolucionar poco? Ni puñetera idea.
Pero si veo muchos caminos posibles de evolució:
- Dónde guarda el programa las palabras y sus significados?
  - Ficheros de texto >>> Por aquí vamos a empezar... nos vale! Es suficiente. No me complico.
                          Pero.. sin perder de vista una cosita: Qué problemas tiene esta decisión? 
                          - Cambiarán las palabras o sus definiciones con el tiempo? NO...
                          - Es decir, me quieres decir, que en día 1 vamos a salir con un diccionario con todas las palabras posibles, con todos los significados posibles y si nunguna errata! ESTO NO VA A PASAR EN LA VIDA!
                          Puedo regenerar el fichero... Y esto implicaría qué? TENGO QUE REDISTRIBUIRLO A TODOS MIS USUARIOS.
                          Es decir, que tienen que REINSTALAR UNA NUEVA VERSION DEL PROGRAMA.
                          - Sabrán ahcerlo?
                          - Lo harán?
                          - Controlo yo ya que versión de mi programa usan?
                          - Y más jodido...


Nunca voy a tener una espeficicación de requisitos perfecta! Eso es una falacia... de eso ya hemos aprendido.. y por eso hoy en dñía trabajamos con metodologías AGILES.

Es decir, formas de trabajo que cuando lleguen CAMBIOS (Y DOY POR SUPUESTO QUE VAN A LLEGAR!) no impacten demasiado en el producto.

Los desarrolaldores pensamos que cuando acabamos un producto, están resueltos todos los problemas del mundo. ACABAO
No nos han enseñado que en ese momento es cuando el producto NACE!
Y que ese producto habrá que mantenerlo años, funcionando 24x7, dando soporte a usuarios.
Y Que eso cuesta ucho más dinero que el desarroolo del producto!! 
Y aqui entra el concepto de TCO (Total Cost of Ownership).

Pon a los tios y tioas de soporte técnico!
Resolviendo incidencias...
- Que no me encuentra esta palabra
- Que me da error al inslar...
De entrada, al ser un programa de consola, QUE SE EJECUTA EN LOCAL, los usuarios son los que lo instalan en su máquina.Eso im
plica que potencialmente habrá muchas versiones simultaneas del programa en funcionamiento!
La gente de soporte lo primero que tienen que preguntar es qué versión tienen instalada.
Y en base a eso tener un banco de respuestas preparado para cada versión del programa.

A unos usuarios de mi empresa les puede dar unos resultados y a otros otros! dferentes! EIN???? si tienen distintas versiones!

ESTO ES UNA LOCURA! ABSOLUTA!
Esta bien como versión 1.
En cuanto pueda quiero los diccionarios en una BBDD central... y que los programas trabajen contra esa BBDD central.
Si hay un cambio en un diccionario, palabra, significado... eso no impacta al programa.. y todos los usuarios trabajan contra la misma verisón de los diccionarios. Y no tienen que reinstalar nada, porque cambien las palabras!

Esto es deseable? EVIDENTEMENTE.

El diseño de una app de consola, tampoco tienen sentido... desto deberá acabar en una app web...Pero empiezo hoy.

Y hoy empiezo por una app de consola, con ficheros!
Y dentro de 2 años, acaberé con una app web con bbdd. Y lo que quiero es no sufrir en el camino!
Es decir, que NO TENGA QUE CAMBIAR NI UNA LINEA DE CODIGO en ese camino. No me importa tirar cosas a la basura (las menos posibles), no me importa crear cosas nuevas (evidentemente las menos posibles), pero no quiero tener que MODIFICAR NI UNA LINEA DE CODIGO!
                                   
No se donde acabará... lo que voy a hacer un diseño que me permita evolucionar el producto.
Y un repo de git, y un archivo de maven y un proyecto java... es un MONOLITO!
Que tenbemos muy claro ya , desde hace décadas que es un mal diseño de cara a su mantenibilidad.

---
Ese programa tiene 2 partes muy claras:
- FRONTAL:          Consola/Linea de comandos: Lógica de comunicación con el usuario
- API DE COMUNICACION! que debe ser un proyecto independiente!
- BACKEND:          Core... Lógica de negocio: Busqueda de palabras y significados en diccionarios de idiomas.

Pregunta... querría poder cambiar el día de mañana el backend (ficheros-> BBDD) sin cambiar el frontal? NO
            debería importarle al frontal el backend que se utilice? NO

            Entonces, por qué juntos?

Tengo la versión 1.0.0 del programa de búsqueda.
Y en esa versión tengo un diccionario de español con 20000 palabras
Y ahora quiero una nueva VERSION del diccionario de Español, con 25000 palabras y algunas de las que había corregidas (tenían erratas)
Esto puede ocurrir? No solo puede ocurrir... OCURRIRÁ! FIJO!
Y meterán diccionario de Inglés... y de ELFICO!!! FIJO!!!!
Quiero que por sacar una nueva versión de un diccionario, ytenga que sacar una versión nueva de la app? NO
Y entonces me vale un REPO DE GIT? Que es donde controlo la versión?
Es más, lo que definirán los diccionarios, definirán el código? Es el mismo perfil?
- Desarrolladores
- Lingüistas
  - Español
  - Inglés
  - ...
Son distintos equipos.
- Diccionario español v1.0.0 . Le meto más palabras -> v1.1.0. Le corrijo palabras -> v1.1.1. Lo llevo a BBDD -> v2.0.0
- Y mientars el diccionario de inglés sigue su ritmo de versiones independiente
- Y mientras, la app sigue su propio ritmo de versiones independiente

Si meto todo en un repo: ESTOY MUY JODIDO!
La cantidad de dolores de cabeza que esto va a dar (y horas de trabajo) será impresionante.

No hay nada más importante que un buen diseño de sistema y de flujo de trabajo.

---

# Api de nuestro backend (Y no hablo de un API REST, ni de microservicios)... hablo de 4 interfaces JAVA y poco más.

```java

import java.util.List;
import java.util.Optional;
import lombok.NonNull;  // Lo que hace es en tiempo de compilacion inyecta código (en este caso un if y un throw new NullPointerException() si el valor es nulo)
                        // Pero la gracia no es solo ahorrar código.. es que en la firma del método quede claro el comportamiento)
                        // Esto tiene que ver con el principio de sustitución de Liskov (SOLID)


public interface Diccionario {

    String getIdioma();

    boolean existe(@NonNull String palabra);

    Optional<List<String>> getSignificados(@NonNull String palabra); // Si me pasas nulo, te lanzo un null pointer exception. no lo soporto el valor nulo.

}

public interface SuministradorDeDiccionarios {

    List<Sttring> getIdiomas();

    boolean tienesDiccionarioDe(@NonNull String idioma);            // Quizás una implementación de esta inferfaz, tenga los idiomas (no los diccionarios) precargados.

    Optional<Diccionario> getDiccionario(@NonNull String idioma); // Devuelve el diccionario correspondiente al idioma solicitado, si existe.

}

```


Esa función tal y como está definida, viola uno de los 5 principio SOLID: Nos estamos meando en el principio de sustitución de Barbara Liskov.
Ninguno sabe como comunicase con esa función. No está definido.
- Qué le tengo que pasar a esa función? Un String.. la palabra
- Qué devuelve la función? Ni puñetera idea.
  - Si pregunto por la palabra melón en español, devolverá: ["fruta", "comestible", "dulce"]
  - Si pregunto por archilovovo qué devuelve? NPI!
    - null          \
    - Lista vacia   / Son ambiguas. Me toca mirar código (si lo poseo) o documentación (si la han documentado) = ABSURDO EN 2026
    - Exception. Cosa buena: Es explicita    Cosa muy mala: Uso una Exception (muy caro computacionalmente para control de flujo de la app)
    Y asbéis por que hay 3 opciones posibles? PORQUE NINGUNA ES BUENA!  Las 3 son una mierda! Si hubiera una buena, todo el mundo usaria esa... y no habría ambigüedad.
    Desde java 1.8 tenemos la OPCION!
    - Desde java 1.8 se considera una muy muy mala práctica que una función devuelva null! No se hace.
    - Aparece la clase Optional. Es una caja... que puede llevar algo dentro o ir vacía... Siempre te devuelven la caja!
         .isEmpty() // Devuelve true si la caja está vacía, false si tiene algo dentro
         .isPresent() // Devuelve true si la caja tiene algo dentro, false si está vacía
         .get() // Devuelve el contenido de la caja si tiene algo dentro, lanza NoSuchElementException si está vacía
         Ls Optional están muy mal entendidos. mucha gente dice que son para evitar NullPointerException.
         La realidad es que sirven para cerrar el contrrato sin ambigüedades. Quien lea la firma de la función (signatura) debe tener claro el comportamiento de la misma... sin ambigüedad, sin necesidad de mirar el código o la documentación.

         De hecho, la realidad es que ni siquiera aún está claro.
         Qué pasa si le paso un null como palabra? Devuelve optional vacio o nullpointer exception? NPI

# Principio de sustitución de liskov 

Dice que si S es una subclase de T, entonces los objetos de tipo T en un programa pueden ser reemplazados por objetos de tipo S sin alterar las propiedades deseables del programa (corrección de errores, exactitud, etc.).

Básicamente, en cristiano esto implica que los contratos deben quedar perfectamente cerrados y definidos.

Si una interfaz no declara si un parámetro admite o no valor nulo, 2 implementaciones de la misma podrían comportarse de manera diferente:
- Una podría lanzar un NullPointerException.
- Otra podría aceptar el valor nulo y devolver un Optional vacío o un nulo.

Eso cambiaría el comportamiento del programa... Lo cual es un problemón de cara a su mantenibilidad.
Esto rompe el principio de sustitución de Liskov, ya que no se puede garantizar que un objeto de la subclase pueda reemplazar a uno de la superclase sin alterar el comportamiento esperado.