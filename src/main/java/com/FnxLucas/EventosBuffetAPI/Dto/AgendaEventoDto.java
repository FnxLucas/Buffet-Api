package com.FnxLucas.EventosBuffetAPI.Dto;

import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaEventoDto {

    private Long id;
    private String nomeEvento;
    private String clienteNome;
    private String clienteTelefone;
    private LocalDate dataEvento;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String localEvento;
    private Integer qtdConvidados;
    private StatusENUM status;

    public AgendaEventoDto() {
    }

    public AgendaEventoDto(Long id, String nomeEvento, String clienteNome, String clienteTelefone,
                           LocalDate dataEvento, LocalTime horarioInicio, LocalTime horarioFim,
                           String localEvento, Integer qtdConvidados, StatusENUM status) {
        this.id = id;
        this.nomeEvento = nomeEvento;
        this.clienteNome = clienteNome;
        this.clienteTelefone = clienteTelefone;
        this.dataEvento = dataEvento;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.localEvento = localEvento;
        this.qtdConvidados = qtdConvidados;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteTelefone() {
        return clienteTelefone;
    }

    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getLocalEvento() {
        return localEvento;
    }

    public void setLocalEvento(String localEvento) {
        this.localEvento = localEvento;
    }

    public Integer getQtdConvidados() {
        return qtdConvidados;
    }

    public void setQtdConvidados(Integer qtdConvidados) {
        this.qtdConvidados = qtdConvidados;
    }

    public StatusENUM getStatus() {
        return status;
    }

    public void setStatus(StatusENUM status) {
        this.status = status;
    }
}
