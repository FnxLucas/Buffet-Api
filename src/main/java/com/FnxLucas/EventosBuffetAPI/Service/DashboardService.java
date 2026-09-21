package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Dto.AgendaEventoDto;
import com.FnxLucas.EventosBuffetAPI.Dto.AgendaResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.PainelResumoDto;
import com.FnxLucas.EventosBuffetAPI.Model.Endereco;
import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Model.Orcamento;
import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;
import com.FnxLucas.EventosBuffetAPI.Model.StatusOrcamentoENUM;
import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import com.FnxLucas.EventosBuffetAPI.Repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public PainelResumoDto gerarResumoComercial(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        Date inicioDate = toStartOfDayDate(dataInicio);
        Date fimDate = toEndOfDayDate(dataFim);

        List<Orcamento> orcamentos;
        List<Evento> eventos;

        if (dataInicio == null && dataFim == null) {
            orcamentos = orcamentoRepository.findAll();
            eventos = eventoRepository.findAll();
        } else {
            orcamentos = orcamentoRepository.findOrcamentosFiltrados(inicioDate, fimDate, dataInicio, dataFim);
            if (inicioDate != null && fimDate != null) {
                eventos = eventoRepository.findByDataEventoBetween(inicioDate, fimDate);
            } else if (inicioDate != null) {
                eventos = eventoRepository.findByDataEventoGreaterThanEqual(inicioDate);
            } else {
                eventos = eventoRepository.findByDataEventoLessThanEqual(fimDate);
            }
        }

        long totalOrcamentos = orcamentos.size();
        long orcamentosAprovados = orcamentos.stream()
                .filter(o -> o.getStatus() == StatusOrcamentoENUM.APROVADO)
                .count();
        long orcamentosRecusados = orcamentos.stream()
                .filter(o -> o.getStatus() == StatusOrcamentoENUM.RECUSADO)
                .count();
        long orcamentosRascunho = orcamentos.stream()
                .filter(o -> o.getStatus() == StatusOrcamentoENUM.RASCUNHO)
                .count();

        Double taxaConversao = totalOrcamentos > 0
                ? Math.round(((double) orcamentosAprovados / totalOrcamentos * 100.0) * 100.0) / 100.0
                : 0.0;

        BigDecimal faturamentoTotal = orcamentos.stream()
                .filter(o -> o.getStatus() == StatusOrcamentoENUM.APROVADO && o.getTotal() != null)
                .map(Orcamento::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal ticketMedio = orcamentosAprovados > 0
                ? faturamentoTotal.divide(BigDecimal.valueOf(orcamentosAprovados), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        long totalEventos = eventos.size();
        long eventosConfirmados = eventos.stream()
                .filter(e -> e.getStatus() == StatusENUM.CONFIRMADO)
                .count();
        long eventosPendentes = eventos.stream()
                .filter(e -> e.getStatus() == StatusENUM.PENDENTE)
                .count();
        long eventosFinalizados = eventos.stream()
                .filter(e -> e.getStatus() == StatusENUM.FINALIZADO)
                .count();
        long eventosCancelados = eventos.stream()
                .filter(e -> e.getStatus() == StatusENUM.CANCELADO)
                .count();

        return new PainelResumoDto(
                totalOrcamentos,
                orcamentosAprovados,
                orcamentosRecusados,
                orcamentosRascunho,
                taxaConversao,
                faturamentoTotal,
                ticketMedio,
                totalEventos,
                eventosConfirmados,
                eventosPendentes,
                eventosFinalizados,
                eventosCancelados,
                dataInicio,
                dataFim
        );
    }

    public AgendaResponseDto obterAgenda(LocalDate dataInicio, LocalDate dataFim, StatusENUM status) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        Date inicioDate = toStartOfDayDate(dataInicio);
        Date fimDate = toEndOfDayDate(dataFim);

        List<Evento> eventos;
        if (inicioDate == null && fimDate == null && status == null) {
            eventos = eventoRepository.findAll();
        } else if (inicioDate != null && fimDate != null && status != null) {
            eventos = eventoRepository.findByDataEventoBetweenAndStatus(inicioDate, fimDate, status);
        } else if (inicioDate != null && fimDate != null) {
            eventos = eventoRepository.findByDataEventoBetween(inicioDate, fimDate);
        } else if (status != null) {
            List<Evento> porStatus = eventoRepository.findByStatus(status);
            if (inicioDate != null) {
                porStatus = porStatus.stream().filter(e -> e.getDataEvento() != null && !e.getDataEvento().before(inicioDate)).collect(Collectors.toList());
            }
            if (fimDate != null) {
                porStatus = porStatus.stream().filter(e -> e.getDataEvento() != null && !e.getDataEvento().after(fimDate)).collect(Collectors.toList());
            }
            eventos = porStatus;
        } else if (inicioDate != null) {
            eventos = eventoRepository.findByDataEventoGreaterThanEqual(inicioDate);
        } else {
            eventos = eventoRepository.findByDataEventoLessThanEqual(fimDate);
        }

        List<AgendaEventoDto> eventosDto = eventos.stream()
                .map(this::converterParaAgendaEventoDto)
                .sorted(Comparator.comparing(AgendaEventoDto::getDataEvento, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(AgendaEventoDto::getHorarioInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        long totalEventosNoPeriodo = eventosDto.size();
        long totalConfirmadosNoPeriodo = eventosDto.stream()
                .filter(e -> e.getStatus() == StatusENUM.CONFIRMADO)
                .count();

        return new AgendaResponseDto(
                eventosDto,
                totalEventosNoPeriodo,
                totalConfirmadosNoPeriodo,
                dataInicio,
                dataFim
        );
    }

    private AgendaEventoDto converterParaAgendaEventoDto(Evento evento) {
        if (evento == null) return null;

        AgendaEventoDto dto = new AgendaEventoDto();
        dto.setId(evento.getId());
        dto.setNomeEvento(evento.getNome());
        dto.setClienteNome(evento.getCliente() != null ? evento.getCliente().getNome() : null);
        dto.setClienteTelefone(null);
        dto.setDataEvento(toLocalDate(evento.getDataEvento()));
        dto.setHorarioInicio(evento.getHorarioInicio() != null ? evento.getHorarioInicio().toLocalTime() : null);
        dto.setHorarioFim(evento.getHorarioFim() != null ? evento.getHorarioFim().toLocalTime() : null);
        dto.setLocalEvento(formatarEndereco(evento.getEndereco()));
        dto.setQtdConvidados(evento.getQtdConvidados());
        dto.setStatus(evento.getStatus());

        return dto;
    }

    private String formatarEndereco(Endereco endereco) {
        if (endereco == null) return null;
        List<String> partes = new ArrayList<>();
        if (endereco.getRua() != null && !endereco.getRua().isBlank()) {
            String logradouro = endereco.getRua();
            if (endereco.getNumero() != null && !endereco.getNumero().isBlank()) {
                logradouro += ", " + endereco.getNumero();
            }
            partes.add(logradouro);
        }
        if (endereco.getCidade() != null && !endereco.getCidade().isBlank()) {
            partes.add(endereco.getCidade());
        }
        return partes.isEmpty() ? null : String.join(" - ", partes);
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Date toStartOfDayDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date toEndOfDayDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }
}
