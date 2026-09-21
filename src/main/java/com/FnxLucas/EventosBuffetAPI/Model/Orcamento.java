package com.FnxLucas.EventosBuffetAPI.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private LocalDate validade;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(columnDefinition = "TEXT")
    private String condicoesComerciais;

    private BigDecimal subtotal;
    private BigDecimal valorFinalManual;
    private Double margemCalculada;
    private BigDecimal desconto;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private StatusOrcamentoENUM status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orcamento_id")
    private List<Item> listaItens = new ArrayList<>();

    public Orcamento() {
    }

    public Orcamento(Long id, Evento evento, LocalDate validade, String observacoes, String condicoesComerciais, BigDecimal subtotal, BigDecimal valorFinalManual, Double margemCalculada, BigDecimal desconto, BigDecimal total, StatusOrcamentoENUM status, List<Item> listaItens) {
        this.id = id;
        this.evento = evento;
        this.validade = validade;
        this.observacoes = observacoes;
        this.condicoesComerciais = condicoesComerciais;
        this.subtotal = subtotal;
        this.valorFinalManual = valorFinalManual;
        this.margemCalculada = margemCalculada;
        this.desconto = desconto;
        this.total = total;
        this.status = status;
        if (listaItens != null) {
            this.listaItens = listaItens;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getCondicoesComerciais() {
        return condicoesComerciais;
    }

    public void setCondicoesComerciais(String condicoesComerciais) {
        this.condicoesComerciais = condicoesComerciais;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getValorFinalManual() {
        return valorFinalManual;
    }

    public void setValorFinalManual(BigDecimal valorFinalManual) {
        this.valorFinalManual = valorFinalManual;
    }

    public Double getMargemCalculada() {
        return margemCalculada;
    }

    public void setMargemCalculada(Double margemCalculada) {
        this.margemCalculada = margemCalculada;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusOrcamentoENUM getStatus() {
        return status;
    }

    public void setStatus(StatusOrcamentoENUM status) {
        this.status = status;
    }

    public List<Item> getListaItens() {
        return listaItens;
    }

    public void setListaItens(List<Item> listaItens) {
        this.listaItens = listaItens;
    }
}
