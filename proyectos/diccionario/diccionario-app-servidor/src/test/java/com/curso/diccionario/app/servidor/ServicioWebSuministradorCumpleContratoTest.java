package com.curso.diccionario.app.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.ContratoSuministradorDeDiccionariosTest;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.impl.servicioweb.SuministradorDeDiccionariosDesdeServicioWeb;

// La implementación web (cliente) contra este servidor de verdad, con el contrato del API.
// Si pasa, cliente + HTTP + controlador se comportan como cualquier otra implementación:
// quien use el API no nota que hay una red por medio.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { ServidorDeDiccionarios.class, ServidorConDatosDePrueba.class },
        properties = "spring.datasource.url=jdbc:h2:mem:contrato")
class ServicioWebSuministradorCumpleContratoTest extends ContratoSuministradorDeDiccionariosTest {

    @Value("${local.server.port}")
    int puerto;

    @Autowired
    ServidorConDatosDePrueba.SuministradorIntercambiable enElServidor;

    @Override
    protected SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos) {
        enElServidor.usar(datos);
        return new SuministradorDeDiccionariosDesdeServicioWeb("http://localhost:" + puerto);
    }
}
