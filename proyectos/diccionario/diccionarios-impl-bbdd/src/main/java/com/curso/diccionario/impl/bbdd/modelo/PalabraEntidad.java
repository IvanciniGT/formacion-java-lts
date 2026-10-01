package com.curso.diccionario.impl.bbdd.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// La restricción única crea además el índice por (idioma, texto), que es justo por lo que se busca.
@Entity
@Table(name = "palabras", uniqueConstraints = @UniqueConstraint(columnNames = { "idioma", "texto" }))
public class PalabraEntidad {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 10)
    private String idioma;

    @Column(nullable = false, length = 200)
    private String texto;

    // OrderColumn: en un diccionario el orden de las acepciones es información (la primera es la principal).
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "palabra_id", nullable = false)
    @OrderColumn(name = "orden")
    private List<SignificadoEntidad> significados = new ArrayList<>();

    // JPA necesita un constructor sin argumentos; protegido para que nadie más cree palabras vacías.
    protected PalabraEntidad() {
    }

    public PalabraEntidad(String idioma, String texto, List<SignificadoEntidad> significados) {
        this.idioma = idioma;
        this.texto = texto;
        this.significados = new ArrayList<>(significados);
    }

    public String getIdioma() {
        return idioma;
    }

    public String getTexto() {
        return texto;
    }

    public List<SignificadoEntidad> getSignificados() {
        return significados;
    }
}
