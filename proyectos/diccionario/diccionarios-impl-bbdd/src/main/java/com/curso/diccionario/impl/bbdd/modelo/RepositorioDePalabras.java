package com.curso.diccionario.impl.bbdd.modelo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioDePalabras extends JpaRepository<PalabraEntidad, Long> {

    Optional<PalabraEntidad> findByIdiomaAndTexto(String idioma, String texto);

    boolean existsByIdiomaAndTexto(String idioma, String texto);

    // Borrado en bloque y no deleteAll(findByIdioma(...)): eso cargaría las 5000 palabras con sus
    // significados y ejemplos para borrarlas una a una. Tres sentencias, de hijos a padres.
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = """
            delete from ejemplos where significado_id in (
                select s.id from significados s join palabras p on s.palabra_id = p.id where p.idioma = :idioma)""")
    void borrarEjemplosDe(@Param("idioma") String idioma);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "delete from significados where palabra_id in (select id from palabras where idioma = :idioma)")
    void borrarSignificadosDe(@Param("idioma") String idioma);

    @Modifying(clearAutomatically = true)
    @Query("delete from PalabraEntidad p where p.idioma = :idioma")
    void borrarPalabrasDe(@Param("idioma") String idioma);
}
