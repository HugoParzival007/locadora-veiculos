package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Float valorLocacao;

    @ManyToMany
    @JoinTable(
            name = "categoria_modelo",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "modelo_id")
    )
    private List<Modelo> listModelos = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nome, Float valorLocacao) {
        this.nome = nome;
        this.valorLocacao = valorLocacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Float getValorLocacao() {
        return valorLocacao;
    }

    public void setValorLocacao(Float valorLocacao) {
        this.valorLocacao = valorLocacao;
    }

    public List<Modelo> getListModelos() {
        return listModelos;
    }

    public void setListModelos(List<Modelo> listModelos) {
        this.listModelos = listModelos;
    }
}