package com.curso.diccionario.api.referencia;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.ContratoSuministradorDeDiccionariosTest;
import com.curso.diccionario.api.contrato.DatosDePrueba;

class SuministradorEnMemoriaCumpleContratoTest extends ContratoSuministradorDeDiccionariosTest {

    @Override
    protected SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos) {
        return new SuministradorEnMemoria(datos);
    }

}
