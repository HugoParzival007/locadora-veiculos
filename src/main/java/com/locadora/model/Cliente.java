package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String cnh;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contato_id")
    private Contato contato;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<ContratoLocacao> contratos = new ArrayList<>();

    public Cliente() {}

    public Cliente(String nome, String cpf, String cnh, Contato contato, Endereco endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.cnh = cnh;
        this.contato = contato;
        this.endereco = endereco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    public Contato getContato() { return contato; }
    public void setContato(Contato contato) { this.contato = contato; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public List<ContratoLocacao> getContratos() { return contratos; }
    public void setContratos(List<ContratoLocacao> contratos) { this.contratos = contratos; }
}