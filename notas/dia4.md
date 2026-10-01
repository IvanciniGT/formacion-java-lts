
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

    app                              √   1.1.0
    diccionarios-api                 √   1.1.0
    diccionarios-impl-ficheros       √   1.1.0
    controlador-rest-api             √   1.0.0
    controlador-rest-impl            √   1.0.0
    app-servidor                     √   1.0.0
    diccionarionario-impl-rest       x   1.0.0

# Para montar el componente diccionarionario-impl-rest

Necesitamos hacer peticiones HTTP al servidor central de diccionarios para obtener y actualizar los datos de los diccionarios.

Y ahí vamos a usar un api nuevo para interactuar con servicios web que ofrece JAVA en sus versiones más recientes.

Se basa este nuevo api en el patrón builder.

Define varias clases:
    - HttpClient: Para realizar las peticiones HTTP.
    - HttpRequest: Para construir las solicitudes HTTP.
    - HttpResponse: Para manejar las respuestas HTTP.
    - HttpHeaders: Para gestionar los encabezados de las solicitudes y respuestas HTTP.

Lo normal es crear un Client utilizando el patrón builder, de la siguiente manera:

```java
HttpClient client = HttpClient.newBuilder()
                              .version(HttpClient.Version.HTTP_2)
                              .build();
```

En paralelo crear una petición, por ejemplo GET: 
```java
HttpRequest request = HttpRequest.newBuilder()
                                 .uri(URI.create("http://servidor-central-diccionarios/diccionario"))
                                 .GET()
                                 .build();
```

O una petición POST, pasando un JSON en el body y parametros en headers:

```java
HttpRequest postRequest = HttpRequest.newBuilder()
                                    .uri(URI.create("http://servidor-central-diccionarios/diccionario"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString("{\"clave\":\"valor\"}"))
                                    .build();
```

Y luego lanzar la petición utilizando el cliente HTTP:

```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());


```
Cuando lanzamos un request, puedo decir qué interes tengo en el body de vuelta.

Por ejemplo, si no me interesa el body de la respuesta, puedo usar:

```java
HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
System.out.println(response.statusCode());
```

También puedo indicar que quiero el body como un stream de bytes, útil para descargar archivos grandes:

```java
HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
try (InputStream inputStream = response.body()) {
    // Procesar el stream de bytes
}
```

Si me viene un JSON, puedo usar un BodyHandler que lo convierta a un String y luego parsearlo con una librería de JSON:

```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
String jsonResponse = response.body();
// Parsear el JSON con una librería como Jackson o Gson
```

Incluso, podemos convertirlo a tipos específicos por ejemplo usando GSON:

```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
String jsonResponse = response.body();
Gson gson = new Gson();
MiClase objeto = gson.fromJson(jsonResponse, MiClase.class); // El JSON debe ser compatible con la estructura de MiClase
```

Es muy cómodo de usar.

Este api se incluye en el paquete `java.net.http`.


---

# Cosas por hacer en nuestro proyecto para que quede guay!

Ordenadas más o menos por dependencias: cada bloque se apoya en los anteriores.

## 1. App de consola: separar la interfaz de usuario

Hoy `BuscarPalabra` valida argumentos y `ProcesadorDePeticiones` hace `System.out.println` por todas partes.
Si mañana queremos pedir los datos por teclado, o sacar los mensajes en inglés, hay que tocar la lógica.

- 2 módulos nuevos:
  - **ui-consola-api**: la interfaz con todo lo que se le dice o se le pide al usuario.
      - `obtenerIdiomaDelUsuario`
      - `obtenerPalabraDelUsuario`
      - `mostrarSignificadosDePalabra`
      - `mostrarPalabraNoEncontrada`
      - `mostrarIdiomaNoEncontrado`
      - `mostrarErrorDeUsoDelPrograma`
      - `mostrarErrorGenerico`
  - **ui-consola-impl**: la nuestra lee idioma y palabra de los argumentos de línea de comandos.
    Podría pedírselos al usuario por teclado (no lo haremos), y precisamente por eso los métodos `obtener...` van en la interfaz.
    Se descubre con `ServiceLoader`, igual que el suministrador.
- `ProcesadorDePeticiones`: toda la lógica, incluida la validación de los argumentos. Habla con la UI y con el suministrador, solo a través de sus interfaces.
- `BuscarPalabra`: sin lógica. Solo monta las piezas (UI + suministrador) y se las pasa al procesador.
- De regalo: el procesador se puede probar con una UI falsa, sin capturar la consola.

## 2. Diccionarios: un proyecto por idioma

Ahora `es.txt` está copiado en la app de consola, en el servidor y en los tests. Cada copia evoluciona por su lado.

- 2 proyectos nuevos: **diccionario-es** y **diccionario-en**. Solo llevan el fichero, en `diccionarios/xx.txt`.
- Al menos 5000 palabras cada uno, las mismas en los 2 idiomas.
  - Ojo con de dónde salen: generadas por nosotros, o de una fuente con licencia que lo permita (los volcados de Wiktionary son CC BY-SA, por ejemplo).
- Son dependencias de quien los quiera tener: la app de consola con ficheros, el servidor, el futuro módulo de BBDD...
  Versionan a su ritmo: corregir una definición es sacar una versión del diccionario, no de la aplicación.

## 3. Ficheros dentro de jars: saber qué idiomas hay

Dentro de un jar no se puede listar una carpeta, solo pedir un fichero por su nombre. Por eso hoy `IDIOMAS_EN_CLASSPATH` va a capón.

- Idea inicial: una variable de entorno con los idiomas disponibles.
  Problema: tiene que ir acorde a las dependencias, y nada garantiza que lo vaya. Se desincroniza a la primera.
- Alternativa sin configuración: que cada jar de idioma lleve un fichero de índice con el mismo nombre en todos, por ejemplo `META-INF/diccionarios/idioma`, con su código (`ES`).
  `ClassLoader.getResources(...)` (en plural) devuelve **todos** los que haya en el classpath: los idiomas disponibles son, por construcción, los jars que hay.
  `META-INF` no es un paquete, así que en el module path no hace falta `opens`.

## 4. Módulo de diccionarios en BBDD (Spring Data JPA)

- Por ahora con H2, para cambiarla más adelante por otra.
- Al arrancar, un `CommandLineRunner` carga los ficheros de los proyectos de idioma (depende de ellos, ver punto 2). Y de forma inteligente:
  - Guarda en una tabla la huella (SHA-256 mejor que MD5) de cada fichero cargado.
  - Si la huella no ha cambiado, no recarga nada.
  - Si viene una versión nueva del fichero, borra las palabras de ese idioma y las recarga, todo en **una transacción**: que nadie vea el idioma a medio cargar.
- Ojo: con H2 **en memoria** la BBDD se vacía en cada arranque, así que la huella no evita nada. Para que tenga sentido, H2 en modo fichero (`jdbc:h2:file:...`).
- "Enchufarlo sin cambiar ni una línea":
  - En el servidor, casi: hoy el `@Bean` de `ConfiguracionDeDiccionarios` crea la implementación de ficheros a mano.
    Para no tocar nada, que la elija la configuración (`@ConditionalOnProperty`, o perfiles de Spring) y que cada implementación traiga su propia autoconfiguración.
  - En la app de consola encaja peor: el `ServiceLoader` crea el suministrador sin Spring, y esta implementación necesita un contexto de Spring para JPA.
    Su sitio natural es el servidor.

## 5. Pruebas

- **impl-web-service**: no tiene ninguna.
  - Pruebas unitarias contra un servidor falso con `com.sun.net.httpserver.HttpServer` (viene en el JDK, no hay que añadir dependencias): 200, cada uno de los 404, 500, servidor caído, timeouts...
  - Y la buena: que **cumpla el contrato del API** (el test-jar de `diccionarios-api`), como ya hace la de ficheros.
    En `crearSuministradorCon(datos)` se levanta el servidor real con la implementación de ficheros sobre esos datos, y se devuelve un suministrador web que apunta a él.
    Si pasa, cliente + HTTP + servidor se comportan igual que la implementación en memoria.
- **controlador-rest**: ya tiene pruebas con `@WebMvcTest`.
- **app-servidor**: una prueba de arranque (`@SpringBootTest`) que compruebe que todo encaja: el bean, los ficheros del classpath, la spec en `/v3/api-docs`.

## 6. Elegir componentes sin editar poms

Hoy, pasar la app de consola de ficheros a servicio web es editar el pom a mano.

- Mejor que dependencias comentadas: **perfiles de Maven** en `app-console` y `app-servidor`, uno por combinación.
  `mvn package -Pficheros` o `mvn package -Pservicio-web`, y se activan/desactivan sin tocar el fichero.
  Un perfil activo por defecto, para que `mvn package` a secas siga funcionando.
- Ojo: con el `ServiceLoader`, si en el module path hay **dos** implementaciones, coge la primera que encuentra. Los perfiles tienen que ser excluyentes.

## 7. README

- La nueva arquitectura (cliente / servidor), los componentes disponibles y cómo combinarlos.
- Ejemplos de comandos (`java` y `mvn`) para cada combinación, incluido el module path completo de cada una (en la de servicio web también van el jar del API REST y Gson).
- Dónde está la especificación OpenAPI: `target/openapi.yaml` del API REST, y `/swagger-ui.html` con el servidor arrancado.

## 8. Deudas que ya conocemos

- **La cache de la implementación de ficheros no es segura entre hilos.** Es un `WeakHashMap`: en la app de consola daba igual (un hilo), pero el servidor atiende peticiones en paralelo.
  Opciones: `ConcurrentHashMap` con `computeIfAbsent`, o cargar todos los diccionarios al arrancar.
  Y, de paso, la cache tampoco libera nada (está en el README, en "Limitaciones conocidas").
- **`getIdiomas()` no puede avisar de un error**: devuelve lista vacía y "no hay idiomas" no se distingue de "el servidor no responde". Hay un TODO en la implementación web.
- **Los métodos `@Deprecated(forRemoval = true)` se quedan**, a propósito: son el ejemplo de cómo evolucionar un API sin romperlo.
  Quitarlos rompería a quien los use: eso ya sería un **major** (diccionarios-api 2.0.0).
- **Errores por `System.out`** en la implementación de ficheros: pasar a `System.Logger`, que viene en el JDK y no añade dependencias.