package com.locadora.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String cargo;

    // Adicionados para seguir o padrão Usuario
    private String login;
    private String senha;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contato_id")
    private Contato contato;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "funcionario")
    private List<ContratoLocacao> contratos = new ArrayList<>();

    public Funcionario() {}

    public Funcionario(String nome, String cpf, String matricula, String cargo, String login, String senha, Contato contato, Endereco endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.contato = contato;
        this.endereco = endereco;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Contato getContato() { return contato; }
    public void setContato(Contato contato) { this.contato = contato; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public List<ContratoLocacao> getContratos() { return contratos; }
    public void setContratos(List<ContratoLocacao> contratos) { this.contratos = contratos; }
}