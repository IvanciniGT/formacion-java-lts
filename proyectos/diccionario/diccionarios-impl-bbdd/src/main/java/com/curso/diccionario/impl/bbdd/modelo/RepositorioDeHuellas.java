package com.curso.diccionario.impl.bbdd.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioDeHuellas extends JpaRepository<HuellaDeFichero, String> {
}
