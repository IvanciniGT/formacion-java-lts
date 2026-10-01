module impl.diccionarios.en.servicio.web {
    requires api.diccionarios;
    requires static lombok;
    // Automatic-Module-Name del jar del API REST: no tiene module-info.
    requires api.controlador.rest;
    // HttpClient no está en java.base.
    requires java.net.http;
    requires com.google.gson;

    // No exporta nada: nadie salvo el ServiceLoader puede crear instancias de sus clases.
    provides com.curso.diccionario.api.SuministradorDeDiccionarios
            with com.curso.diccionario.impl.servicioweb.SuministradorDeDiccionariosDesdeServicioWeb;
}
