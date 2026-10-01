package com.curso.diccionario.impl.bbdd;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDeHuellas;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDePalabras;

// Lo que hace que baste con añadir el jar: Spring Boot la encuentra en
// META-INF/spring/...AutoConfiguration.imports y monta todo sin que la aplicación lo nombre.
//
// @AutoConfigurationPackage: añade este paquete a los que Spring Boot escanea buscando entidades y
// repositorios. Sin él solo miraría el paquete de la aplicación, y este jar está en otro.
// Antes que JPA: los paquetes tienen que estar apuntados cuando JPA los busque.
@AutoConfiguration(beforeName = {
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration" })
@AutoConfigurationPackage
public class AutoConfiguracionDeDiccionariosEnBBDD {

    @Bean
    AlmacenDeDiccionarios almacenDeDiccionarios(RepositorioDePalabras palabras, RepositorioDeHuellas huellas) {
        return new AlmacenDeDiccionarios(palabras, huellas);
    }

    // ConditionalOnMissingBean: si la aplicación (o una prueba) trae sus propios ficheros, se usan los suyos.
    @Bean
    @ConditionalOnMissingBean
    FuenteDeDiccionarios fuenteDeDiccionarios() {
        return new FuenteDeDiccionariosEnClasspath();
    }

    @Bean
    CargadorDeDiccionarios cargadorDeDiccionarios(FuenteDeDiccionarios fuente, AlmacenDeDiccionarios almacen) {
        return new CargadorDeDiccionarios(fuente, almacen);
    }

    @Bean
    @ConditionalOnMissingBean
    SuministradorDeDiccionarios suministradorDeDiccionarios(AlmacenDeDiccionarios almacen) {
        return new SuministradorDeDiccionariosEnBBDD(almacen);
    }
}
