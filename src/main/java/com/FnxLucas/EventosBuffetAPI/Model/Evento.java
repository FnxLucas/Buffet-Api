package com.FnxLucas.EventosBuffetAPI.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Date dataEvento;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioFim;
    private int qtdConvidados;

    @Enumerated(EnumType.STRING)
    private StatusENUM status;

    @Embedded
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    public Evento() {
    }

    public Evento(Long id, String nome, Date dataEvento, LocalDateTime horarioInicio, LocalDateTime horarioFim, int qtdConvidados, StatusENUM status, Endereco endereco, Cliente cliente) {
        this.id = id;
        this.nome = nome;
        this.dataEvento = dataEvento;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.qtdConvidados = qtdConvidados;
        this.status = status;
        this.endereco = endereco;
        this.cliente = cliente;
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

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDateTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalDateTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalDateTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalDateTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public int getQtdConvidados() {
        return qtdConvidados;
    }

    public void setQtdConvidados(int qtdConvidados) {
        this.qtdConvidados = qtdConvidados;
    }

    public StatusENUM getStatus() {
        return status;
    }

    public void setStatus(StatusENUM status) {
        this.status = status;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
