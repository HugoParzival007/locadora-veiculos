package com.locadora.model;

import com.locadora.model.enums.StatusVeiculo;
import jakarta.persistence.*;


@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusVeiculo status;

    private Integer km;
    private String placa;
    private String chassi;
    private String renavam;
    private String cor;

    @ManyToOne
    private Modelo modelo;

    public Veiculo() {
        this.status = StatusVeiculo.DISPONIVEL;
    }

    public Veiculo(Modelo modelo, StatusVeiculo status, Integer km, String placa, String chassi, String renavam, String cor) {
        this.modelo = modelo;
        this.status = status;
        this.km = km;
        this.placa = placa;
        this.chassi = chassi;
        this.renavam = renavam;
        this.cor = cor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public String getRenavam() {
        return renavam;
    }

    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}
