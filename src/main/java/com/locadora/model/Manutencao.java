package com.locadora.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Manutencao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Temporal(TemporalType.DATE)
    private Date data;

    private Float custo;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    // CONSTRUTOR VAZIO
    public Manutencao() {}

    public Manutencao(String descricao, Date data, Float custo, Veiculo veiculo) {
        this.descricao = descricao;
        this.data = data;
        this.custo = custo;
        this.veiculo = veiculo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Float getCusto() {
        return custo;
    }

    public void setCusto(Float custo) {
        this.custo = custo;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }
}