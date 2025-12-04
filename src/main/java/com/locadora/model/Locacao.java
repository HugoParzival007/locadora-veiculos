package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataRetirada;

    @Temporal(TemporalType.DATE)
    private Date dataDevolucao;

    private Float valorLocacao;

    @OneToMany(mappedBy = "locacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Ocorrencia> listaOcorrencias = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    // CONSTRUTOR VAZIO - OBRIGATÓRIO
    public Locacao() {}

    public Locacao(Date dataRetirada, Date dataDevolucao, Float valorLocacao, Veiculo veiculo) {
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.valorLocacao = valorLocacao;
        this.veiculo = veiculo;
    }

    public void addOcorrencia(Ocorrencia ocorrencia) {
        listaOcorrencias.add(ocorrencia);
        ocorrencia.setLocacao(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(Date dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Float getValorLocacao() {
        return valorLocacao;
    }

    public void setValorLocacao(Float valorLocacao) {
        this.valorLocacao = valorLocacao;
    }

    public List<Ocorrencia> getListaOcorrencias() {
        return listaOcorrencias;
    }

    public void setListaOcorrencias(List<Ocorrencia> listaOcorrencias) {
        this.listaOcorrencias = listaOcorrencias;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }
}