package com.FnxLucas.EventosBuffetAPI.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PainelResumoDto {

    private long totalOrcamentos;
    private long orcamentosAprovados;
    private long orcamentosRecusados;
    private long orcamentosRascunho;
    private Double taxaConversao;
    private BigDecimal faturamentoTotal;
    private BigDecimal ticketMedio;

    private long totalEventos;
    private long eventosConfirmados;
    private long eventosPendentes;
    private long eventosFinalizados;
    private long eventosCancelados;

    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    public PainelResumoDto() {
        this.faturamentoTotal = BigDecimal.ZERO;
        this.ticketMedio = BigDecimal.ZERO;
        this.taxaConversao = 0.0;
    }

    public PainelResumoDto(long totalOrcamentos, long orcamentosAprovados, long orcamentosRecusados, long orcamentosRascunho,
                           Double taxaConversao, BigDecimal faturamentoTotal, BigDecimal ticketMedio,
                           long totalEventos, long eventosConfirmados, long eventosPendentes,
                           long eventosFinalizados, long eventosCancelados,
                           LocalDate periodoInicio, LocalDate periodoFim) {
        this.totalOrcamentos = totalOrcamentos;
        this.orcamentosAprovados = orcamentosAprovados;
        this.orcamentosRecusados = orcamentosRecusados;
        this.orcamentosRascunho = orcamentosRascunho;
        this.taxaConversao = taxaConversao;
        this.faturamentoTotal = faturamentoTotal;
        this.ticketMedio = ticketMedio;
        this.totalEventos = totalEventos;
        this.eventosConfirmados = eventosConfirmados;
        this.eventosPendentes = eventosPendentes;
        this.eventosFinalizados = eventosFinalizados;
        this.eventosCancelados = eventosCancelados;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
    }

    public long getTotalOrcamentos() {
        return totalOrcamentos;
    }

    public void setTotalOrcamentos(long totalOrcamentos) {
        this.totalOrcamentos = totalOrcamentos;
    }

    public long getOrcamentosAprovados() {
        return orcamentosAprovados;
    }

    public void setOrcamentosAprovados(long orcamentosAprovados) {
        this.orcamentosAprovados = orcamentosAprovados;
    }

    public long getOrcamentosRecusados() {
        return orcamentosRecusados;
    }

    public void setOrcamentosRecusados(long orcamentosRecusados) {
        this.orcamentosRecusados = orcamentosRecusados;
    }

    public long getOrcamentosRascunho() {
        return orcamentosRascunho;
    }

    public void setOrcamentosRascunho(long orcamentosRascunho) {
        this.orcamentosRascunho = orcamentosRascunho;
    }

    public Double getTaxaConversao() {
        return taxaConversao;
    }

    public void setTaxaConversao(Double taxaConversao) {
        this.taxaConversao = taxaConversao;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public long getTotalEventos() {
        return totalEventos;
    }

    public void setTotalEventos(long totalEventos) {
        this.totalEventos = totalEventos;
    }

    public long getEventosConfirmados() {
        return eventosConfirmados;
    }

    public void setEventosConfirmados(long eventosConfirmados) {
        this.eventosConfirmados = eventosConfirmados;
    }

    public long getEventosPendentes() {
        return eventosPendentes;
    }

    public void setEventosPendentes(long eventosPendentes) {
        this.eventosPendentes = eventosPendentes;
    }

    public long getEventosFinalizados() {
        return eventosFinalizados;
    }

    public void setEventosFinalizados(long eventosFinalizados) {
        this.eventosFinalizados = eventosFinalizados;
    }

    public long getEventosCancelados() {
        return eventosCancelados;
    }

    public void setEventosCancelados(long eventosCancelados) {
        this.eventosCancelados = eventosCancelados;
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
