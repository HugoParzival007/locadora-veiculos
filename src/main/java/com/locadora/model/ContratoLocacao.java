package com.locadora.model;

import com.locadora.model.enums.StatusContrato;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ContratoLocacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataContrato;
    private float valorCaucao;
    private float valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusContrato status;

    // AQUI ESTÁ A CORREÇÃO DO ERRO (fetch = FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contrato_id")
    private List<Locacao> listaLocacao = new ArrayList<>();

    @ManyToOne
    private Funcionario funcionario;
    @ManyToOne
    private Cliente cliente;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    public ContratoLocacao() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDataContrato() { return dataContrato; }
    public void setDataContrato(Date dataContrato) { this.dataContrato = dataContrato; }
    public float getValorCaucao() { return valorCaucao; }
    public void setValorCaucao(float valorCaucao) { this.valorCaucao = valorCaucao; }
    public StatusContrato getStatus() { return status; }
    public void setStatus(StatusContrato status) { this.status = status; }
    public List<Locacao> getListaLocacao() { return listaLocacao; }
    public void setListaLocacao(List<Locacao> listaLocacao) { this.listaLocacao = listaLocacao; }
    public float getValorTotal() { return valorTotal; }
    public void setValorTotal(float valorTotal) { this.valorTotal = valorTotal; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
}