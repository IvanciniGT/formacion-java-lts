package com.curso.diccionario.app.servidor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.controlador.rest.ControladorRestDiccionarios;
import com.curso.diccionario.controlador.rest.GestorDeErroresRest;
import com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros;

// El único sitio que sabe que los diccionarios salen de ficheros.
// Cambiar a otra implementación (BBDD...) es cambiar este @Bean y la dependencia del pom.
@Configuration
// El controlador vive en otro paquete y el escaneo de @SpringBootApplication no llega:
// se importa explícitamente, así se ve qué piezas monta la aplicación.
@Import({ ControladorRestDiccionarios.class, GestorDeErroresRest.class })
public class ConfiguracionDeDiccionarios {

    @Bean
    SuministradorDeDiccionarios suministradorDeDiccionarios(@Value("${diccionarios.carpeta}") String carpeta) {
        return new SuministradorDeDiccionariosDesdeFicheros(carpeta);
    }
}
