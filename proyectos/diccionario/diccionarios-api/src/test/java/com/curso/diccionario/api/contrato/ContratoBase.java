package com.curso.diccionario.api.contrato;

import static com.curso.diccionario.api.contrato.SignificadoDePrueba.significado;

import com.curso.diccionario.api.SuministradorDeDiccionarios;

/**
 * Lo único que una implementación tiene que aportar para ejecutar las pruebas de contrato:
 * un suministrador que contenga exactamente los datos que se le piden.
 */
public abstract class ContratoBase {

    public static final SignificadoDePrueba MELON_FRUTO =
            significado("Fruto grande, redondo y de pulpa jugosa y dulce.");
    public static final SignificadoDePrueba MELON_PERSONA =
            significado("Persona con pocas luces.", "Eres un melón", "No seas melón");
    public static final SignificadoDePrueba PERA_FRUTO =
            significado("Fruto del peral.");
    public static final SignificadoDePrueba MELON_EN =
            significado("A large round fruit with sweet juicy flesh.", "A slice of melon");

    protected abstract SuministradorDeDiccionarios crearSuministradorCon(DatosDePrueba datos);

    // Dos idiomas para poder comprobar que un diccionario no ve las palabras de otro,
    // y un significado sin ejemplos para comprobar que se devuelve lista vacía, no null.
    public static DatosDePrueba datosHabituales() {
        return new DatosDePrueba()
                .conPalabra("ES", "melón", MELON_FRUTO, MELON_PERSONA)
                .conPalabra("ES", "pera", PERA_FRUTO)
                .conPalabra("EN", "melon", MELON_EN);
    }

}
