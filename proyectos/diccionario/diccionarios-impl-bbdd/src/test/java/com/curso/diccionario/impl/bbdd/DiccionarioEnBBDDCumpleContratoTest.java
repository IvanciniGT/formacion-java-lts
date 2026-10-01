package com.curso.diccionario.impl.bbdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.ContratoDiccionarioTest;
import com.curso.diccionario.api.contrato.DatosDePrueba;

@SpringBootTest(classes = AplicacionDePruebas.class)
class DiccionarioEnBBDDCumpleContratoTest extends ContratoDiccionarioTest {

    @Autowired
    AlmacenDeDiccionarios almacen;

    @Autowired
    SuministradorDeDiccionarios suministrador;

    @Override
    protected SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos) {
        CargaDeDatosDePrueba.cargar(almacen, datos);
        return suministrador;
    }
}
