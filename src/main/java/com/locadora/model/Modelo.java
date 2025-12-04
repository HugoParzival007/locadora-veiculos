package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "modelo")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Temporal(TemporalType.DATE)
    @Column(name = "ano_modelo")
    private Date ano;

    @Column(name = "quantidade_modelo")
    private Integer qtModelo;

    @OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL)
    private List<Veiculo> listVeiculo = new ArrayList<>();

    // RELACIONAMENTO COM CATEGORIA (Muitos para Muitos)
    @ManyToMany
    @JoinTable(
            name = "categoria_modelo",
            joinColumns = @JoinColumn(name = "modelo_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias = new ArrayList<>();

    // RELACIONAMENTO COM MARCA (Muitos para Um) - CORRIGIDO
    // A chave estrangeira 'marca_id' fica AQUI na tabela modelo
    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    public Modelo() {}

    public Modelo(String nome, Date ano) {
        this.nome = nome;
        this.ano = ano;
        this.qtModelo = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getAno() { return ano; }
    public void setAno(Date ano) { this.ano = ano; }

    public Integer getQtModelo() { return qtModelo; }
    public void setQtModelo(Integer qtModelo) { this.qtModelo = qtModelo; }

    public List<Veiculo> getListVeiculo() { return listVeiculo; }
    public void setListVeiculo(List<Veiculo> listVeiculo) { this.listVeiculo = listVeiculo; }

    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }

    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }

    public void addVeiculo(Veiculo veiculo) {
        listVeiculo.add(veiculo);
        veiculo.setModelo(this);
    }
}