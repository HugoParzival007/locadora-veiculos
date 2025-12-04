package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marca")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    // mappedBy="marca" refere-se ao atributo 'private Marca marca;' na classe Modelo
    @OneToMany(mappedBy = "marca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Modelo> listModelo = new ArrayList<>();

    public Marca() {}

    public Marca(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Modelo> getListModelo() { return listModelo; }
    public void setListModelo(List<Modelo> listModelo) { this.listModelo = listModelo; }

    // Método auxiliar essencial para vincular os dois lados da relação
    public void addModelo(Modelo modelo) {
        listModelo.add(modelo);
        modelo.setMarca(this); // Isso garante que o ID da marca seja salvo no modelo
    }
}