package com.FnxLucas.EventosBuffetAPI.Dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendaResponseDto {

    private List<AgendaEventoDto> eventos = new ArrayList<>();
    private long totalEventosNoPeriodo;
    private long totalConfirmadosNoPeriodo;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    public AgendaResponseDto() {
    }

    public AgendaResponseDto(List<AgendaEventoDto> eventos, long totalEventosNoPeriodo, long totalConfirmadosNoPeriodo,
                             LocalDate periodoInicio, LocalDate periodoFim) {
        this.eventos = eventos != null ? eventos : new ArrayList<>();
        this.totalEventosNoPeriodo = totalEventosNoPeriodo;
        this.totalConfirmadosNoPeriodo = totalConfirmadosNoPeriodo;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
    }

    public List<AgendaEventoDto> getEventos() {
        return eventos;
    }

    public void setEventos(List<AgendaEventoDto> eventos) {
        this.eventos = eventos != null ? eventos : new ArrayList<>();
    }

    public long getTotalEventosNoPeriodo() {
        return totalEventosNoPeriodo;
    }

    public void setTotalEventosNoPeriodo(long totalEventosNoPeriodo) {
        this.totalEventosNoPeriodo = totalEventosNoPeriodo;
    }

    public long getTotalConfirmadosNoPeriodo() {
        return totalConfirmadosNoPeriodo;
    }

    public void setTotalConfirmadosNoPeriodo(long totalConfirmadosNoPeriodo) {
        this.totalConfirmadosNoPeriodo = totalConfirmadosNoPeriodo;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
    }
}
