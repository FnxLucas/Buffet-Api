package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Model.Item;
import com.FnxLucas.EventosBuffetAPI.Model.Orcamento;
import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;
import com.FnxLucas.EventosBuffetAPI.Model.StatusOrcamentoENUM;
import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import com.FnxLucas.EventosBuffetAPI.Repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private DocxGeneratorService docxGeneratorService;

    public void validarEvento(Orcamento orcamento) {
        if (orcamento == null || orcamento.getEvento() == null || orcamento.getEvento().getId() == null) {
            throw new IllegalArgumentException("O orçamento deve estar associado a um evento válido.");
        }
        Long eventoId = orcamento.getEvento().getId();
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        orcamento.setEvento(evento);
    }

    public Orcamento calcularValores(Orcamento orcamento) {
        if (orcamento == null) {
            throw new IllegalArgumentException("O orçamento não pode ser nulo.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        if (orcamento.getListaItens() != null) {
            for (Item item : orcamento.getListaItens()) {
                if (item != null) {
                    Integer qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
                    BigDecimal valUnit = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
                    BigDecimal valorTotalItem = valUnit.multiply(BigDecimal.valueOf(qtd));
                    item.setValorTotal(valorTotalItem);
                    subtotal = subtotal.add(valorTotalItem);
                }
            }
        }
        orcamento.setSubtotal(subtotal);

        BigDecimal valorFinalManual = orcamento.getValorFinalManual();
        if (valorFinalManual != null && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferenca = valorFinalManual.subtract(subtotal);
            BigDecimal margem = diferenca.divide(subtotal, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            orcamento.setMargemCalculada(margem.doubleValue());
        } else {
            orcamento.setMargemCalculada(0.0);
        }

        BigDecimal desconto = orcamento.getDesconto() != null ? orcamento.getDesconto() : BigDecimal.ZERO;
        orcamento.setDesconto(desconto);

        BigDecimal baseParaTotal = valorFinalManual != null ? valorFinalManual : subtotal;
        BigDecimal total = baseParaTotal.subtract(desconto);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
        orcamento.setTotal(total);

        return orcamento;
    }

    public Orcamento salvarOrcamento(Orcamento orcamento) {
        validarEvento(orcamento);
        if (orcamento.getStatus() == null) {
            orcamento.setStatus(StatusOrcamentoENUM.RASCUNHO);
        }
        calcularValores(orcamento);
        return orcamentoRepository.save(orcamento);
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    public Optional<Orcamento> buscarPorId(Long id) {
        return orcamentoRepository.findById(id);
    }

    public List<Orcamento> buscarPorEvento(Long eventoId) {
        return orcamentoRepository.findByEventoId(eventoId);
    }

    public Orcamento atualizarOrcamento(Long id, Orcamento request) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

        if (request.getEvento() != null && request.getEvento().getId() != null) {
            Evento evento = eventoRepository.findById(request.getEvento().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
            existente.setEvento(evento);
        }

        existente.setValidade(request.getValidade());
        existente.setObservacoes(request.getObservacoes());
        existente.setCondicoesComerciais(request.getCondicoesComerciais());
        existente.setValorFinalManual(request.getValorFinalManual());
        existente.setDesconto(request.getDesconto());

        if (request.getStatus() != null) {
            existente.setStatus(request.getStatus());
        }

        if (request.getListaItens() != null) {
            existente.getListaItens().clear();
            existente.getListaItens().addAll(request.getListaItens());
        }

        calcularValores(existente);
        return orcamentoRepository.save(existente);
    }

    public Orcamento aprovarOrcamento(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        orcamento.setStatus(StatusOrcamentoENUM.APROVADO);
        if (orcamento.getEvento() != null) {
            Evento evento = orcamento.getEvento();
            evento.setStatus(StatusENUM.CONFIRMADO);
            eventoRepository.save(evento);
        }
        return orcamentoRepository.save(orcamento);
    }

    public Orcamento recusarOrcamento(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        orcamento.setStatus(StatusOrcamentoENUM.RECUSADO);
        return orcamentoRepository.save(orcamento);
    }

    public void deletarOrcamento(Long id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Orçamento não encontrado");
        }
        orcamentoRepository.deleteById(id);
    }

    public byte[] gerarPropostaDocx(Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        return docxGeneratorService.gerarPropostaDocx(orcamento);
    }
}
