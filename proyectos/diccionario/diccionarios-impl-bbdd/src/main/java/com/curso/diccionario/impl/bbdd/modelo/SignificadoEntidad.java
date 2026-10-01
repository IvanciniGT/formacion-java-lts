package com.curso.diccionario.impl.bbdd.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "significados")
public class SignificadoEntidad {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 1000)
    private String texto;

    // Los ejemplos son solo texto, sin identidad propia: colección de valores, no entidad.
    @ElementCollection
    @CollectionTable(name = "ejemplos", joinColumns = @JoinColumn(name = "significado_id"))
    @OrderColumn(name = "orden")
    @Column(name = "ejemplo", nullable = false, length = 1000)
    private List<String> ejemplos = new ArrayList<>();

    protected SignificadoEntidad() {
    }

    public SignificadoEntidad(String texto, List<String> ejemplos) {
        this.texto = texto;
        this.ejemplos = new ArrayList<>(ejemplos);
    }

    public String getTexto() {
        return texto;
    }

    public List<String> getEjemplos() {
        return ejemplos;
    }
}
