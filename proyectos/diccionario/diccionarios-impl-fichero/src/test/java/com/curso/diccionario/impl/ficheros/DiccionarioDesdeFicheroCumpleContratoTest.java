package com.curso.diccionario.impl.ficheros;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.ContratoDiccionarioTest;
import com.curso.diccionario.api.contrato.DatosDePrueba;

class DiccionarioDesdeFicheroCumpleContratoTest extends ContratoDiccionarioTest {

    @TempDir
    Path carpeta;

    @Override
    protected SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos) {
        FicherosDePrueba.escribir(datos, carpeta);
        return new SuministradorDeDiccionariosDesdeFicheros(carpeta.toString());
    }

}
