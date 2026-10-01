

Novedades en versiones JAVA LTS:

# Java 1.8

- Soporte programación funcional:
  - Paquete `java.util.function`, Con interfaces funcionales como `Function`, `Predicate`, `Consumer` y `Supplier`.
  - Nuevo operador `::` para referencias a métodos y constructores.
  - Nuevo operador `->` para expresiones lambda, forma alternativa de definir funciones anónimas, dentro de una expresión.
- Se incluye una implementación del modelo de programación Map-Reduce:
  - Paquete `java.util.stream` con la clase `Stream` y métodos como `map`, `filter`, `reduce`, `collect`, etc.
  - Posibilidad de convertir colecciones en streams mediante el método `stream()` de la interfaz `Collection`.
  - Posibilidad de convertir streams en colecciones mediante métodos como `collect(Collectors.toList())`, `collect(Collectors.toSet())`, `collect(Collectors.toMap())`, etc.
- Inclusión de `Optional`:
  - Paquete `java.util` con la clase `Optional` y métodos como `of`, `ofNullable`, `empty`, `isPresent`, `ifPresent`, `orElse`, `orElseGet`, `orElseThrow`, etc.
    Esto sirve para quitar ambigüedades de la firma de las funciones.
    Desde este momento se considera una muy mala práctica devolver `null` en funciones.
- Métodos default en interfaces:
  - Permiten definir una implementación por defecto para los métodos en las interfaces.
  - Se declaran usando la palabra clave `default` seguida de la implementación del método.
  - Facilitan la evolución de las interfaces sin romper las implementaciones existentes.
  - No las usamos para lógica compartida entre implementaciones, ya que su propósito principal es permitir la evolución de las interfaces sin romper el código existente. Para eso otro están las clases abstractas.
- Métodos públicos estáticos en interfaces.
    - Los métodos estáticos son métodos que NO DEPENDEN DE UNA INSTANCIA... no usan `this`
    - Por ejemplo, funciones auxiliares, funciones de utilidad...
    - Esas funciones solo se podían poner el clases. Y era un absurdo conceptual.
  - Las clases están pensadas para generar instancias de ellas : new Clase()
  - Y hay veces que creaba clases que solo tenían métodos estáticos: Librería de utilidades.
  - Eso obligaba a definir la clase como final y a poner un constructor privado para evitar que se instanciara.
  - Es absurdo tener que crear clases solo para agrupar métodos estáticos.
  - De hecho esta es de las cagadas que tiene Java en su sintaxis.
  - Cualquier (o muchos otros ) lenguajes permiten crear ficheros con fucniones (librerías de utilidades) sin necesidad de crear clases solo para agrupar métodos estáticos. En python, en js, en ruby, en ts se puede hacer directamente.
  - Lo mejor que se les ocurrió fue permitir métodos estáticos en interfaces, para que se puedan agrupar funciones auxiliares sin necesidad de crear clases solo para eso.
  - Problema... los pendejos hicieron solo métodos estáticos públicos. Y si mi función tiene 500 lineas? No puedo partirla en 5 funciones internas? Pues no!Para eso tuve que esperar a java 9 -> Java 11 LTS
- Hasta java 1.8 (antes de java 1,8), las fechas eran horribles:
  - java.util.Date
  - java.util.Calendar
  - java.sql.Date
  - System.currentTimeMillis() -> long con la cantidad de milisegundos desde el 1 de enero de 1970 (epoch)
  Era un rollo.
  Había una librería java cojonuda, que usaba mucha gente, pero era externa al api de java.. la hizo un tio por ahí! 
  Esa librería se llamaba Joda-Time.
  En java 1.8 se llega a un acuerdo con el autor de Joda-Time y se incorpora una nueva API de fechas y horas en el paquete `java.time:
    - `java.time.LocalDate`
    - `java.time.LocalTime`
    - `java.time.LocalDateTime`
    - `java.time.ZonedDateTime`
    - `java.time.Instant`
    - `java.time.Duration`
    - `java.time.Period`
    - `java.time.format.DateTimeFormatter`

    Hay cierta compatibilidad entre `java.time` y las clases antiguas como `java.util.Date` y `java.util.Calendar`.
    Por ejemplo, se pueden convertir entre `java.util.Date` y `java.time.Instant` usando los métodos `toInstant()` y `Date.from(Instant)`.

    Es muy cómoda:
    ```java
    LocalDate fechaActual = LocalDate.now();
    LocalDate fechaNacimiento = LocalDate.of(1990, Month.JANUARY, 1);
    Period edad = Period.between(fechaNacimiento, fechaActual);
    System.out.println("Edad: " + edad.getYears() + " años");
    // Suma 4 meses
    fechaActual = fechaActual.plusMonths(4);
    System.out.println("Fecha actual más 4 meses: " + fechaActual);
    ```

    Problema... muchos drivers de BBDD en versiones más antiguas no soportaban bien `java.time` y había que convertir a las clases antiguas como `java.util.Date` para poder trabajar con ellos.

    En general, con las versiones más recientes de Java y los drivers de BBDD, `java.time` se soporta correctamente y no es necesario recurrir a las clases antiguas.

# Java 11 (recoge los cambios de 9 y 10)

- Proyecto jigsaw (modularización de aplicaciones Java y de la JVM, introducido en Java 9)
  - Los ficheros `module-info.java` definen los módulos y sus dependencias.
  - Evolución del ServiceLoader para soportar módulos.
- Métodos privados estáticos en interfaces
- Métodos `.of` en las colecciones (`List.of`, `Set.of`, `Map.of`) para crear colecciones inmutables de forma concisa.
- `var` para inferencia de tipos locales (introducido en Java 10). Es cómoda en algunos casos...
  En otros, el problema es que no veo el tipo... y queda oscuro.
  Si tengo una función que llama a otras 2:
    ```java
    public int miFuncion() {
        var resultado1 = funcion1();      // Y esta función devuelve un tipo Map<String, List<String>> Demasiado...
                                          // Y el tipo no me aaporta... cojo el dato y lo paso a otro.
        int resultado2 = funcion2(resultado1);
    }
    ```
- Api nuevo para peticiones HTTP: `java.net.http.HttpClient` (introducido en Java 11) para realizar solicitudes HTTP de manera más sencilla y moderna que con `HttpURLConnection`:
  - HttpClient se puede usar para enviar solicitudes GET, POST, etc., y manejar respuestas de manera asíncrona o síncrona.
    ```java
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/data"))
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.body());
    ```
- Otras cambios mínimo:
  - Files tiene nuevos métodos para trabajar con rutas y archivos de manera más sencilla, como `Files.writeString`, `Files.readString`, `Files.mismatch`, entre otros.
  - String tiene nuevos métodos como `isBlank`, `lines`, `strip`, `repeat`, entre otros.


# Java 17

- Sealed classes y interfaces: permiten restringir qué otras clases o interfaces pueden extender o implementar una clase o interfaz sellada. Esto proporciona un mayor control sobre la jerarquía de clases y mejora la seguridad y el mantenimiento del código.
- Patrones en instanceof: permiten combinar la comprobación de tipo y el casting en una sola operación, haciendo el código más conciso y legible.
   ```java
    // Antiguamente
    if (obj instanceof String) {
        String s = (String) obj;
        // hacer algo con s
    }
    // Ahora con patrones en instanceof
    if (obj instanceof String s) {
        // hacer algo con s
    }
    ```
- records: permiten definir clases inmutables de manera concisa. Un record automáticamente genera constructores, métodos `equals`, `hashCode` y `toString`.
  ```java

  public record Persona(String nombre, int edad) {}

  // Sería equivalente a:
  public final class Persona {
      private final String nombre;
      private final int edad;

      public Persona(String nombre, int edad) {
          this.nombre = nombre;
          this.edad = edad;
      }

      public String nombre() {
          return nombre;
      }

      public int edad() {
          return edad;
      }

      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          Persona persona = (Persona) o;
          return edad == persona.edad && nombre.equals(persona.nombre);
      }

      @Override
      public int hashCode() {
          return Objects.hash(nombre, edad);
      }

      @Override
      public String toString() {
          return "Persona{" +
                  "nombre='" + nombre + '\'' +
                  ", edad=" + edad +
                  '}';
      }
  }
  ```
- Text blocks:

```java
    // Antes
    String texto = "Línea 1\nLínea 2\nLínea 3";
    // O lo que es peor:
    String texto = "Línea 1" +
                   "\nLínea 2" +
                   "\nLínea 3";

    // Ahora con text blocks (Java 13+)
    String texto = """
                   Línea 1
                   Línea 2
                   Línea 3
                   """; // Vamos... la triple comilla que ha existido de toda la vida en lenguajes como Python.
```

- Switch expressions: permiten usar `switch` como una expresión que devuelve un valor, haciendo el código más conciso y legible.
  Son equivalentes a los condicionales (ifs) como expresión:
  ```java
  int resultado;
  if (condicion1) {  // If como statements
      resultado = 1; // Statement
  } else {
      resultado = 2; // Statement
  }
  // If como expresión
  int resultado = condicion1 ? 1 : 2;
                            // ^   ^
                            // Expresiones
 
  // Los switch solo los teníamos como statements:
    int resultado;
    switch (valor) {
        case 1:
            resultado = 1; // Statement
            break;          // el break cortaba la ejecución del case
            // Si no poníamos break
            // la ejecución continuaría al siguiente case (fall-through)
        case 2:
            resultado = 2; // Statement
            break;
        default:
            resultado = 0; // Statement
            break;
    }

    // Hoy en día podemos escribirswitch como expresiones:
    int resultado = switch (valor) {
        case 1 -> 1; // El valor después de la flecha es la expresión que se devuelve
        case 2 -> {
            // aqui podría hacercosas intermedias
            int valorIntermedio = 42*valor; // Ejemplo de variable intermedia
            yield valorIntermedio;          // El yield ocupa simbólicamente la posicion del break en un switch tradicional
                                            // es el valoir que se devuelve en esa rama del case
        };
        default -> 0;
    };

  ```
- Mejoras significativas en la JVM y su rendimiento, gracias a fundamentalmente nuevos motores del garbage collector y optimizaciones en la ejecución del bytecode.
