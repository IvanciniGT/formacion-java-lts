package com.curso.diccionario.api.referencia;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.ContratoDiccionarioTest;
import com.curso.diccionario.api.contrato.DatosDePrueba;

class DiccionarioEnMemoriaCumpleContratoTest extends ContratoDiccionarioTest {

    @Override
    protected SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos) {
        return new SuministradorEnMemoria(datos);
    }

}
