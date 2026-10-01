package com.curso.diccionario.impl.bbdd;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

// Una librería no tiene aplicación: las pruebas montan una mínima. Con @EnableAutoConfiguration
// entra la autoconfiguración de este módulo, igual que en el servidor; y con H2 en el classpath,
// Spring Boot crea una BBDD en memoria (y distinta para cada contexto).
@SpringBootConfiguration
@EnableAutoConfiguration
class AplicacionDePruebas {
}
