package com.curso.diccionario.app.servidor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.controlador.rest.ControladorRestDiccionarios;
import com.curso.diccionario.controlador.rest.GestorDeErroresRest;
import com.curso.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFicheros;

@Configuration
// El controlador vive en otro paquete y el escaneo de @SpringBootApplication no llega:
// se importa explícitamente, así se ve qué piezas monta la aplicación.
@Import({ ControladorRestDiccionarios.class, GestorDeErroresRest.class })
public class ConfiguracionDeDiccionarios {

    // La implementación por defecto: los ficheros de los jars de idioma, en memoria.
    //
    // Se aparta sola si está el módulo de BBDD (perfil bbdd), que trae su propio suministrador.
    // Por el nombre de la clase y no con @ConditionalOnMissingBean: esta configuración se procesa
    // antes que las autoconfiguraciones, así que todavía no vería el bean de la BBDD.
    @Bean
    @ConditionalOnMissingClass("com.curso.diccionario.impl.bbdd.SuministradorDeDiccionariosEnBBDD")
    SuministradorDeDiccionarios suministradorDeDiccionarios(@Value("${diccionarios.carpeta}") String carpeta) {
        return new SuministradorDeDiccionariosDesdeFicheros(carpeta);
    }
}
