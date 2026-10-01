
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