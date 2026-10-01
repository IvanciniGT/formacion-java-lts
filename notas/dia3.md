# Modelo de programación map reduce.. y algunas de sus funciones

Vamos a empezar por la función map y la función reduce.


## Concepto general

Partimos de una colección sobre la que podemos aplicar MapReduce (en java: Streams).

    Stream<Integer> numeros = Stream.of(1, 2, 3, 4, 5);


Imaginad que quiero una coleccion nueva con el doble de los números
En java tradicional:

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 5);

List<Integer> dobles = new ArrayList<>();
for(Integer n : numeros) {
    dobles.add(n * 2);
}
```


En los Streams tenemos un método llamado map... Ese método permite transformar uno a uno todos los elementos de una colecció, para dar lugar a otra coleccion, con los elementos transformados.
La gracia es que nosotros aportamos la lógica de transformación, y el método map se encarga de aplicarla:
Es algo así como si el método map por dentro hicier:
```java
public List<R> map(List<T> originales, Function<T, R> funcionDeMapeo) {
    List<R> transformados = new ArrayList<>();
    for(T o : originales) {
        transformados.add(funcionDeMapeo.aplicar(o));
    }
    return transformados;
}
```

Con streams y programación mapReduce:
```java

    public Integer doble(Integer numero) {
        return numero * 2;
    }

    Stream<Integer> numeros = Stream.of(1, 2, 3, 4, 5);
    numeros.map(  MiClase::doble  );




    Stream<Integer> numeros = Stream.of(   1, 2, 3, 4, 5         ); // [1,2,3,4,5]
    Stream<Integer> dobles = numeros.map(  numero -> numero * 2  ); // [2,4,6,8,10]
    // Todo algoritmo ram-reduce, debe acabar con una función de reducción.
    int sumatorio = dobles.reduce(0, (a, b) -> a + b);

        // Partimos de estos numeros : 2,4,6,8,10
        // Y los juntamos 2 a 2 usando la suma
        // 2 + 8 = 10
        // 10 + 6 = 16
        // 10 + 16 = 26
        // 26 + 4 = 30

Lo que hemos hecho es un sumatorio de los números.
Podríamos hacer un count, mediante una fucnión de reducción que sume 1 por cada elemento.
    long count = dobles.reduce(0, (a, b) -> a + 1);

Esto normalmente no lo hacemos... ya existe una función llamada count:
    long count = dobles.count(); (por dentro lo que hace es esa reducción)
```

Operación conmutativa en matemáticas (2 operandos)

    a * b == b * a
    a + b == b + a

Operación asociativa en matemáticas (3 operandos)

    (a * b) * c == a * (b * c)
    (a + b) + c == a + (b + c)  


---

Colección inicial     map     Colección intermedia  map     Colección intermedia 2     filter            Colección intermedia 3
--------------------> x2 --------------------------> -2  --------------------------> (predicado) --------------------------------> SUMA -> 20
1                                   2                                0                  n>5                     6
2                                   4                                2                                          14
3                                   6                                4
4                                   8                                6
8                                   16                               14

Cómo haríamos esto en programación tradicional:

# Opción 1... Muy ineficiente... 4 bucles?

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 8);

List<Integer> dobles = new ArrayList<>();
for(Integer numero : numeros) {
    dobles.add(numero * 2);
}

List<Integer> menosDos = new ArrayList<>();
for(Integer numero : dobles) {
    menosDos.add(numero - 2);
}

List<Integer> mayoresDeCinco = new ArrayList<>();
for(Integer numero : menosDos) {
    if(numero > 5) {
        mayoresDeCinco.add(numero);
    }
}

int suma = 0;
for(Integer numero : mayoresDeCinco) {
    suma += numero;
}
System.out.println(suma); // 20
```

# Opción 2... más eficiente: 1 solo bucle!

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 8);
int suma = 0;
for(Integer numero : numeros) {
    int doble = numero * 2;
    int menosDos = doble - 2;
    if(menosDos > 5) {
        suma += menosDos;
    }
}
System.out.println(suma); // 20
```


# Con Streams:

```java
int suma = numeros.stream()
    .map(numero -> numero * 2)
    .map(numero -> numero - 2)
    .filter(numero -> numero > 5)
    .reduce(0, Integer::sum);
System.out.println(suma); // 20
```
 Si esto se comporta como os he dicho arriba, sería eficiente? NADA!

 Algo no encaja si resulta que este modelo de progamación se inventó precisamente para hacer operaciones sobre volumenes gigantes de datos.
 Gigantes: Billones de elementos.


Hay truco.

Imaginad una colección de 1 billón de elementos.
Y le aplico las transformaciones;
```java
    Stream<Integer> original = Stream.of(1, 2, 3, 4, 8 /* ... y así un cien mil billones */); // 100.000.000.000.000.000
    Stream<Integer> dobles = original.map( numero -> numero * 2 );                        // Cuánto pensaís que tardaría en ejecutarse esta operación?
    Stream<Integer> menosDos = dobles.map( numero -> numero - 2 );                                  // Más de 1 segundo
    Stream<Integer> mayoresDeCinco = menosDos.filter( numero -> numero > 5 );                       // Más de 1 minuto
    int suma = mayoresDeCinco.reduce( 0, Integer::sum );                                            // Más de 1 hora
```

Stream<Integer> dobles = original.map( numero -> numero * 2 );                        // Cuánto pensaís que tardaría en ejecutarse esta operación?
NO TARDA NADA! Nada es nada! nanosegundos

Cómo puede ser?

Las funciones map ... y es la gracia del modelo de programaicón map-reduce, se ejecutan en modo LAZY (perezoso). Es decir, no se ejecutan realmente hasta que su resultado es necesario.
original.map( numero -> numero * 2 );  ESTO NO HA MULTIPLICADO x2 los números.
                                        Lo único que ha hecho es apuntar que sobre los números hay que hacer la operación x2

Si os imagináis la colección original como una carpeta llena de papeles (datos), loúnico que ha hecho la función map es DEVOLVER LA MISMA CARPETA con un postit en la caratura que dice:
    ANTES DE USAR , MULTIPLICAR x 2

dobles.map( numero -> numero - 2 );  

    NUEVO POSIT ENCIMA DEL ANTERIOR, que dice: 
    ANTES DE USAR , RESTAR 2

menosDos.filter( numero -> numero > 5 ); 

    NUEVO POSIT ENCIMA DEL ANTERIOR, que dice: 
    ANTES DE USAR , COMPROBAR SI ES MAYOR QUE 5, s no lo es, descartar EL ELEMENTO

mayoresDeCinco.reduce( 0, Integer::sum ); Y AQUI EMPIEZA LA FIESTA

Las funciones de tipo reduce se ejecutan en modo eager (ansioso). Es decir, se ejecutan inmediatamente.

Pero claro.. para sumar los números, necesito los números que tengo que sumar... 
y para ello necesito saber si son mayores que 5 o no
Pero para saber si son mayores que 5, primero tengo que restarles 2...
Pero para restarles 2, primero tengo que multiplicarlos por 2...

La función reduce es la que finalmente dispara la ejecución de todas las operaciones anteriores. Es decir, hasta que no llamamos a reduce, las operaciones map y filter no se ejecutan realmente.

Y es al aplicar la función reduce cuando el motor de procesamiento map-reduce decide la forma en la que aplica todas las operaciones pendientes (map y filter) sobre los datos.

Y sería algo así:
```java
    List<Integer>  original = List.of(1, 2, 3, 4, 8 /* ... y así un cien mil billones */); // 100.000.000.000.000.000
    int suma = 0;
    for (Integer numero : original) {
        if(mayorQueCinco(menos2(doble(numero)))) {
            suma += doble(numero);
        }
    }
```

El motor de procesamiento no hace 4 bucles... Lo optimiza y hace 1.
Y el resultado de una operación map, lo pasa como entrada a la siguiente operación (ya sea otro map, un filter o un reduce).


Cuando entendemos la mecánica, es muy simple transformar datos mediante Map-Reduce.
Eso si.. necesitamos sobre todo:
    1. Familiaridad con la sintxis de programación funcional en Java.
    2. Conocer las funciones map y reduce que tenemos disponibles en Java.

---

# Vamos a montar el sistema de trending topics de twitter (X)

Lo que haremos será analizar y procesar los tweets que van llegando a lo largo del tiempo (en una hora). Extraeré sus hashtags y contaremos cuántas veces aparece cada uno para determinar cuáles son los trending topics.

> Colección inicial:

 - En la playa con mis amigos #SummerLove#GoodVibes
 - Disfrutando de un café en la mañana #CoffeeTime #GoodVibes
 - Noche de películas de miedo con amigos #MovieNight#Friends#MierdaDeMiedo
 - Caminata por el parque #NatureLovers#HealthyLiving. #GoodVibes

Lista de palabras prohibidas: CASA, CULO, PEDO, PIS, MIERDA!
Si un hashtag contiene una de esas palabras... fuera de la lista.. que somo americanos y por ende Superiores moralmente al resto de los individuos, lo descartaremos automáticamente.

> Resultado esperado:
  Tabla:
  | Hashtag.      | Veces   |
  |---------------|---------|
  | GoodVibes     | 3       |
  | SummerLove    | 1       |
  | CoffeeTime    | 1       |
  | MovieNight    | 1       |
  | Friends       | 1       |
  ----------------------------- Queremos solo los 5 primeros

---

# Procedimiento:

> PARTIMOS DE: 
 - "En la playa con mis amigos #SummerLove#GoodVibes"
 - "Disfrutando de un café en la mañana #CoffeeTime #GoodVibes"
 - "Noche de películas de miedo con amigos #MovieNight#Friends#MierdaDeMiedo"
 - "Caminata por el parque #NatureLovers#HealthyLiving. #GoodVibes"

    List<String> Donde cada String era un tweet original.

> Paso0: replace ("#", " #")      TRANSFORMAR UN ELEMENTO EN OTRO: Función MAP
 - "En la playa con mis amigos  #SummerLove #GoodVibes"
 - "Disfrutando de un café en la mañana  #CoffeeTime #GoodVibes"
 - "Noche de películas de miedo con amigos  #MovieNight #Friends #MierdaDeMiedo"
 - "Caminata por el parque  #NatureLovers #HealthyLiving.  #GoodVibes"

    List<String> Donde cada String es un tweet, con hashtags separados entre si

> Paso 1: split(por todo lo que no sea una letra ni un #)      TRANSFORMAR UN ELEMENTO EN OTRO: Función MAP treet -> Lista de términos
 - ["En", "la", "playa", "con", "mis", "amigos", "#SummerLove", "#GoodVibes"]
 - ["Disfrutando", "de", "un", "café", "en", "la", "mañana", "#CoffeeTime", "#GoodVibes"]
 - ["Noche", "de", "películas", "de", "miedo", "con", "amigos", "#MovieNight", "#Friends", "#MierdaDeMiedo"]
 - ["Caminata", "por", "el", "parque", "#NatureLovers", "#HealthyLiving", "#GoodVibes"]

    List<List<String>>   Donde cada String es una palabra o un hashtag del tweet correspondiente.

> Paso 2: Juntar todas esas listas en una sola lista      NO TRANSFORMO UNA LISTA EN OTRA COSA... Esto es otro tema... Estamos juntando listas... en una
>                                                         Esta operación en map-reduce se denomina flatten -> Aplanado
>                                                         En Java Streams, no existe la función flatte per sé. Existe flatMap = .map + .flatten

    - En
    - La
    - Playa
    - Con
    - Mis
    - Amigos
    - #SummerLove
    - #GoodVibes
    - Disfrutando
    - De
    - Un
    - Café
    - En
    - La
    - Mañana
    - #CoffeeTime
    - #GoodVibes
    - Noche
    - De
    - Películas
    - De
    - Miedo
    - Con
    - Amigos
    - #MovieNight
    - #Friends
    - #MierdaDeMiedo
    - Caminata
    - Por
    - El
    - Parque
    - #NatureLovers
    - #HealthyLiving
    - #GoodVibes

    List<String>   Donde cada String es una palabra o un hashtag de todos los tweets, en una sola lista.

> Paso 3: Quedarme con los hashtags      FILTRAR ELEMENTOS DE UNA LISTA: Función FILTER      .startsWith("#")

    - #SummerLove
    - #GoodVibes
    - #CoffeeTime
    - #GoodVibes
    - #MovieNight
    - #Friends
    - #MierdaDeMiedo
    - #NatureLovers
    - #HealthyLiving
    - #GoodVibes

    List<String>   Donde cada String es un hashtag de todos los tweets, en una sola lista.

> Paso 4: Quitar los hashtags malsonantes FILTRO:

    Necesito mirar el qué? No es mirar si el hastag está en la lista de as palabras prohibidas...
    Es mirar si alguna palabra de la lista de palabras prohibidas está contenida en el hashtag.

        ["Caca","Culo","Pedo", "Pis", "Mierda"] -> Filter( palabraProhibida -> hashtag.contains(palabraProhibida)) ->
         ["Mierda"] -> count() > 0
         Si el count es mayor es cero, no quiero el hashtag, lo filtro.

            hashtags.filter( hashtag -> palabrasProhibidas.filter( palabraProhibida -> hashtag.contains(palabraProhibida)).count() == 0)
                             No necesito escribir este chorizo.... Hay una función que me resuelve la papeleta: anyMatch

                             anyMatch hace un filter, seguido de un count() > 0, pero de manera más eficiente.

                             noneMatch hace un filter seguido de un count() == 0

                             allMatch hace un filter seguido de un count() == total, es decir, todos cumplen la condición.

                             Hay luego muchas operaciones map/Reduce que son combinaciones de funciones map reduce simples.


    - #SummerLove
    - #GoodVibes
    - #CoffeeTime
    - #GoodVibes
    - #MovieNight
    - #Friends
    - #NatureLovers
    - #HealthyLiving
    - #GoodVibes

    List<String>   Donde cada String es un hashtag politicamente correcto, como nosotros

> Paso 5: Quitar los # de los hashtags para quedarme solo con las palabras clave  MAP -> hashtag.substring(1)

    - SummerLove
    - GoodVibes
    - CoffeeTime
    - GoodVibes
    - MovieNight
    - Friends
    - NatureLovers
    - HealthyLiving
    - GoodVibes

    List<String>   Donde cada String es un tema! / TOPIC

> Paso 6 : Contar la frecuencia de cada tema  GROUP BY -> count

    - SummerLove: 1
    - GoodVibes: 3
    - CoffeeTime: 1
    - MovieNight: 1
    - Friends: 1
    - NatureLovers: 1
    - HealthyLiving: 1

> Paso 7: Ordenar los temas por frecuencia  SORT BY -> count() DESC

    - GoodVibes: 3
    - SummerLove: 1
    - CoffeeTime: 1
    - MovieNight: 1
    - Friends: 1
    - NatureLovers: 1
    - HealthyLiving: 1

> Paso 7.5 .limit(5)      Quedarme solo los 5 temas más frecuentes

    - GoodVibes: 3
    - SummerLove: 1
    - CoffeeTime: 1
    - MovieNight: 1
    - Friends: 1

> Paso 8: Transformar los datos en un formato adecuado para visualización o exportación  MAP -> (tema, frecuencia) -> { "tema": tema, "frecuencia": frecuencia }
   REDUCCION!


---

# Sorpresa sorpresa!

```java
int suma = numeros.stream()  // Con un billón de elementos
    .map(numero -> numero * 2)
    .map(numero -> numero - 2)
    .filter(numero -> numero > 5)
    .reduce(0, Integer::sum);
System.out.println(suma); // 20
```

Quiero multiplicarlos, restarles cosas, compararlos, sumarlos...

Qué recurso hardware de mi máquina estoy llevando al límite?
Mejor dicho..  cuál es el recurso limitante para que esto vaya rápido!

Pura CPU... RAM necesito la suficiente para poner los datos... MAS RAM no mejora rendimiento.

Más CPU que le eche a la máquina si mejoraría el rendimiento?
> Qué significa más cpu?

Tengo un cpu i5 cojonudo (2 cores con hyperthreading) a una velocidad de 3.5 GHz.
Tengo un cpu xeon cojonudo.. como el mio: 
        2,3 GHz Intel Xeon W de 18 núcleos (con hyperthreading)

Donde más rápido? En el i5.
Y de hecho, no veríamos pasar en el i5 la cpu del 25%      y se pasaría 20 minutos...
Y en mi máquina no veríamos pasar la cpu del 1/36% = 3%... y se pasaría 30 minutos...

Por qué pasa esto? Poque nuestro programa cántos hilos está ejecutando? Threads? 1
Y 1 hilo se ejecuta en un solo core de la CPU... y de hecho ni consume el 100% del core... si tiene hyperthreading, solo el 50% del core.
ABSURDO.. Me hegastado una pasta en cpu... y estoy como un pendejo calentando silla esperando con la cpu al 3%? 

Para aprovechar la potencia de cálculo de mi cpu, necesitaría abrir 36 hilos concurrentes.
Para aprovechar la de un i5 con 2 cores y hyperthreading, necesitaría abrir 4 hilos concurrentes.

Qué tal lo de abrir hilos en java? Y sincronizar resultados/procesos....? Es fácil? NADA FACIL.
Tendría que manejar `Thread`, `Runnable`, `synchronized`, `Locks`, `Executors`... un lío.

Ahora bien... con los Streams (Y el modelo de programación map-reduce) se han lucido.
Para esto nace el modelo de programación map-reduce....
Es un. modelo que soporta nativamente la ejecución paralela de operaciones sobre grandes volúmenes de datos, distribuyendo el trabajo entre múltiples hilos de manera eficiente.



```java
int suma = numeros //.stream().parallel()  
    .parallelStream()
    .map(numero -> numero * 2)
    .map(numero -> numero - 2)
    .filter(numero -> numero > 5)
    .reduce(0, Integer::sum);
System.out.println(suma); // 20
```

Eso ya en automático abre tantos hilos como cores tenga mi CPU, aprovechando al máximo la capacidad de procesamiento paralelo disponible. Internamente lo gestiona todo!


---

Necesitamos leer cada linea    

- OPCION 1:    Files.readAllLines(Paths.get("coleccion_inicial.txt")).stream()
- OPCION 2:    Files.readString(Paths.get("coleccion_inicial.txt")).lines()

> FICHERO: COLECCION INICIAL

- "melón=Fruto grande, redondo y de pulpa jugosa y dulce.|Persona con pocas luces.(Eres un melón)(No seas melón)"
- "banco=Asiento largo en el que caben varias personas.(Nos sentamos en un banco del parque)|Entidad que guarda dinero y concede préstamos.(He pedido una hipoteca al banco)|Conjunto numeroso de peces que nadan juntos.(Vimos un banco de sardinas)"
- "gato=Mamífero felino doméstico.(El gato duerme en el sofá)|Herramienta para levantar pesos, sobre todo coches.(Saca el gato del maletero para cambiar la rueda)|Persona nacida en Madrid.(Mi abuela es gata de toda la vida)"
- "hoja=Órgano verde y plano de las plantas.(En otoño se caen las hojas)|Lámina de papel.(Dame una hoja para apuntarlo)|Cuchilla de un arma o herramienta.(La hoja del cuchillo está mellada)"
- "sierra=Herramienta con una hoja dentada para cortar.(Corta la tabla con la sierra)|Cordillera de montes.(Pasamos el fin de semana en la sierra)"
- "cabo=Extremo de una cosa.(Ata los dos cabos de la cuerda)|Lengua de tierra que entra en el mar.(Llegamos al cabo de Gata)|Militar de rango inmediatamente superior al soldado.(El cabo pasó revista)"

---

> PASO 1: generar el mapa!

    lineas.collect(Collectors.toMap( funcionQueGeneraLaClaveDesdeElContenidoOriginal , funcionQueGeneraElValorDesdeElContenidoOriginal ));
                                        linea -> linea.split("=")[0]
    // No hay funcones map... aplicamos directa la función reduce

---


> RESULTADO:

Map<String, List<Significado>> 
    String            =  palabra (linea.split("=")[0])
    List<Significado> =  ???


funcionQueGeneraElValorDesdeElContenidoOriginal                     Debe recibir la linea entera y devolver una List<Significado>
    linea => "melón=Fruto grande, redondo y de pulpa jugosa y dulce.|Persona con pocas luces.(Eres un melón)(No seas melón)"    

        linea -> linea.split("=")[1]

               "Fruto grande, redondo y de pulpa jugosa y dulce.|Persona con pocas luces.(Eres un melón)(No seas melón)"
        
            .split("\\|")

                [
                    "Fruto grande, redondo y de pulpa jugosa y dulce.",         ---> Cada linea hay que transformarla en un objeto SignificadoDesdeFichero
                    "Persona con pocas luces.(Eres un melón)(No seas melón)"
                ] 

                Lo qwue devuelve split es un array... me interesa transformarlo en un String[] -> Stream<String> (para poder aplicar map reduce  y transformar los strings en objetos SignificadoDesdeFichero) Y a su vez transformar el String[] -> List<SignificadoDesdeFichero>

                String[] -> Stream<String> ... lo podemos hacer con Arrays.stream(array)

        Arrays.stream(linea.split("=")[1].split("\\|"))         -> Stream<String>            Donde cada String es una linea con significado + ejemplos todo junto...

        El trabajo ahora es convertir cada String del Stream<String> en un List<SignificadoDesdeFichero>
        Y para eso aplicamos un MAP.


        Necesito una funcion que reciba un String con significado + ejemplos y devuelva un objeto SignificadoDesdeFichero.

        Arrays.stream(linea.split("=")[1].split("\\|")).map( FUNCION_GENERACION_SIGNIFICADO ).collect(Collectors.toList());


        FUNCION_GENERACION_SIGNIFICADO 
            Recibe: STRING               "Persona con pocas luces.(Eres un melón)(No seas melón)"
            Devuelve:                    SignificadoDesdeFichero

                                         new SignificadoDesdeFichero(texto, listaEjemplos)


```java
public Significado generarSignificadoDesdeTexto(String textoConEjemplos) {
    String[] partes                 = textoConEjemplos.split("\\(|\\)");
    String texto                    = partes[0].trim();
                                        //  ["Persona con pocas luces.","Eres un melón)","No seas melón)"]
    List<String> listadoEjemplos    = Arrays.stream(partes).skip(1).collect(Collectors.toList()); // tengo que quitar el primero
                                    // ["Persona con pocas luces.","Eres un melón","No seas melón"]

    return new SignificadoDesdeFichero(texto, listadoEjemplos);
}



textoConEjemplos -> {
    String[] partes                 = textoConEjemplos.split("\\(|\\)");
    String texto                    = partes[0].trim();
    List<String> listadoEjemplos    = Arrays.stream(partes).skip(1).collect(Collectors.toList()); // tengo que quitar el primero
    return new SignificadoDesdeFichero(texto, listadoEjemplos);
}



```

//public record SignificadoDesdeFichero(String texto, List<String> ejemplos) implements Significado {}

Resultado final:
```java

Map<String, List<Significado>> palabrasConSignificados = 
   Files.readAllLines(Paths.get("coleccion_inicial.txt"))                                                                  // Leo las lineas del fichero
        .stream()                                                                                                          // Para cada linea
        .filter( linea -> !linea.trim().isEmpty() )                                                                        // Quito las lineas en blanco
        .collect(Collectors.toMap(                                                                                         // Convierto enentradas de un mapa
            linea -> linea.split("=")[0] ,                                                                                 // Cuya clave es la palabra (lo de antes del "=")
                                                                                                                           // Cuyo valor es una lista de significados
            linea -> Arrays.stream(linea.split("=")[1].split("\\|"))                                                       // Cojo lo de detras del = y separo por |
                            .map(                                                                                          // Transformo ese array
                                textoConEjemplos -> {
                                    String[] partes                 = textoConEjemplos.split("\\(|\\)");                   // Partiendo para cada item por ()
                                    String texto                    = partes[0].trim();                                    // Lo de antes de los () es el significado
                                    List<String> listadoEjemplos    = Arrays.stream(partes).skip(1).collect(Collectors.toList()); // Lo de detras de los () son los ejemplos
                                    return new SignificadoDesdeFichero(texto, listadoEjemplos);                        // que uso para crear el Objeto SignificadoDesdeFichero
                                }                                                         
                            )   
                            .collect(Collectors.toList())                                                               // al final, entrego los significados como una lista
        ));
```


---

# Estado actual:

- Componente app
- Componente diccionario api
- Componente diccionario impl fichero

Podríamos querer ir evolucionando la app.
Vamos a hacerle evoluciones.

    Version actual: 1.0.0

        Cliente
        ----------------------------------------------------------------
        app -> diccionariosapi -> diccionariosimplfichero
                                        |
                                        v
                                   ficheros de diccionarios en local
                                    es.txt
                                    en.txt

Y funciona!

Tiene graves problemas de mantenibilidad y operación.
Cualquier cambio en los ficheros de diccionarios requieren redistribuir la aplicacióny reinstalarla a todo el mundo = FOLLON!

Además si hay incidencias hay que controlar (SOPORTE TECNICO) qué versión de los ficheros de diccionario estaba en uso en cada cliente que tenga incidencia.
    = MUCHA PASTA!

    Version nueva: 2.0.0

    En esta versión: La lógica de los diccionarios (búsquedas) y los ficheros de diccionarios a un servidor central.


        Cliente                                                     Servidor central de diccionarios
        ---------------------------------------------------         ----------------------------------------------
        app -> diccionariosapi -> diccionarionario-impl-rest -> rest ->  controlador-rest ----->     diccionariosapi  -> diccionariosimplfichero
                                                                 api                                                            |
                                                                                                                                v
                                                                                                                        ficheros de diccionarios en local
                                                                                                                            es.txt
                                                                                                                            en.txt

                                Que pasa?
                             Sirve tal cual          No sirve para nada         Sirve pero con mofidicaciones
app                               √
diccionariosapi                   √
diccionariosimplfichero           √

Necesito 2 piezas nuevas, pero no tengo que tocar ni una linea dde código existente.

Estas piezas nuevas son:
- diccionarionario-impl-rest          Transforma llamadas hechas mediante al api de diccionarios a peticiones http/rest
- controlador-rest                    Transformar peticiones http/rest a llamadas al api de diccionarios

Para hacer esto, ya que tenemos 2 componentes en comunicación, que es lo primero que debería definir? El api de comunicación.
En nuestro caso, no es un api JAVA, sino un api HTTP/REST.
Con que se definen los apis HTTP/Rest? Hay una especificación, antiguamente llamada Swagger(v1, v2), ahora conocida como OpenAPI (sería la v3 de swagger)
Lo que pasa es que al final, lo que ponga en el swagger (documento json o yaml) es lo que debe ofrecer el controlado-rest...
Y no quiero trabajar por duplicado... definiendo cosas en una spec y definiendo cosas en JAVA.
Qué problema tiene esto? Varios:
- Mantenibilidad: Un día cambiaré una cosa (JAVA) y me olvidaré de actualizar la spec (o viceversa)
- Trabajo el doble
- Además, puedo tener un error, y teclear mal un nombre, de forma que quede distinto en la spec y en el código.

Aplicamos otro de lo grandes rincipios de desarrollo de software aqui: DRY (Don't Repeat Yourself)
Lo que vamos a hacer es un API del Controlador REST (Spring)... Y Usar una librería que genere automáticamente la spec OpenAPI: Springdoc


    Version nueva:3.0.0

        Cliente                                                     Servidor central de diccionarios
        ---------------------------------------------------         ----------------------------------------------
        app -> diccionariosapi -> diccionarionario-impl-rest ->     controlador-rest ----->     diccionariosapi  -> diccionariosimplbbdd
                                                                                                                            |
                                                                                                                            v
                                                                                                                    ficheros de diccionarios 
                                                                                                                    en local
                                                                                                                        es.txt
                                                                                                                        en.txt

---



- Componente app                            1.0.0
- Componente diccionario api                1.0.0
  - Al meter Exceptions que no había previsto      --> 1.1.0
- Componente diccionario impl fichero       1.0.0

Cada componente tiene su version independientes... Y POR ESO QUIERO 3 repos de git separados.



---

vA.B.C   <- Esquema semántico de versiones    semver
    A: Major (breaking changes)
    B: Minor (nuevas funcionalidades compatibles)
    C: Patch (corrección de errores)