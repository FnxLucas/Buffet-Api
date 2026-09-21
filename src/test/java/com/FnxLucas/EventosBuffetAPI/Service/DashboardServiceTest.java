package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Dto.AgendaEventoDto;
import com.FnxLucas.EventosBuffetAPI.Dto.AgendaResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.PainelResumoDto;
import com.FnxLucas.EventosBuffetAPI.Model.*;
import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import com.FnxLucas.EventosBuffetAPI.Repository.OrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Cliente clientePadrao;
    private Endereco enderecoPadrao;
    private Evento evento1;
    private Evento evento2;
    private Evento evento3;
    private Orcamento orcamento1;
    private Orcamento orcamento2;
    private Orcamento orcamento3;

    @BeforeEach
    void setUp() {
        enderecoPadrao = new Endereco("São Paulo", "Av. Paulista", "1000");
        clientePadrao = new Cliente(1L, "Lucas Eventos", "12.345.678/0001-90", "contato@lucas.com", enderecoPadrao);

        Date dataEvento1 = Date.from(LocalDate.of(2026, 10, 15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dataEvento2 = Date.from(LocalDate.of(2026, 10, 20).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dataEvento3 = Date.from(LocalDate.of(2026, 10, 25).atStartOfDay(ZoneId.systemDefault()).toInstant());

        evento1 = new Evento(1L, "Casamento Real", dataEvento1,
                LocalDateTime.of(2026, 10, 15, 18, 0),
                LocalDateTime.of(2026, 10, 15, 23, 0),
                150, StatusENUM.CONFIRMADO, enderecoPadrao, clientePadrao);

        evento2 = new Evento(2L, "Festa Corporativa", dataEvento2,
                LocalDateTime.of(2026, 10, 20, 19, 0),
                LocalDateTime.of(2026, 10, 20, 23, 30),
                80, StatusENUM.PENDENTE, enderecoPadrao, clientePadrao);

        evento3 = new Evento(3L, "Aniversário 15 Anos", dataEvento3,
                LocalDateTime.of(2026, 10, 25, 20, 0),
                LocalDateTime.of(2026, 10, 26, 2, 0),
                100, StatusENUM.FINALIZADO, enderecoPadrao, clientePadrao);

        orcamento1 = new Orcamento(1L, evento1, LocalDate.of(2026, 10, 1),
                "Obs 1", "Condicoes 1",
                new BigDecimal("10000.00"), null, null, BigDecimal.ZERO,
                new BigDecimal("10000.00"), StatusOrcamentoENUM.APROVADO, new ArrayList<>());

        orcamento2 = new Orcamento(2L, evento2, LocalDate.of(2026, 10, 5),
                "Obs 2", "Condicoes 2",
                new BigDecimal("5000.00"), null, null, BigDecimal.ZERO,
                new BigDecimal("5000.00"), StatusOrcamentoENUM.APROVADO, new ArrayList<>());

        orcamento3 = new Orcamento(3L, evento3, LocalDate.of(2026, 10, 10),
                "Obs 3", "Condicoes 3",
                new BigDecimal("3000.00"), null, null, BigDecimal.ZERO,
                new BigDecimal("3000.00"), StatusOrcamentoENUM.RECUSADO, new ArrayList<>());
    }

    @Test
    @DisplayName("Gerar Resumo Comercial - Sem filtros de período (Geral)")
    void gerarResumoComercial_SemFiltros_Sucesso() {
        Orcamento orcamento4 = new Orcamento(4L, evento1, LocalDate.of(2026, 10, 12),
                "Obs 4", "Condicoes 4",
                new BigDecimal("2000.00"), null, null, BigDecimal.ZERO,
                new BigDecimal("2000.00"), StatusOrcamentoENUM.RASCUNHO, new ArrayList<>());

        Evento evento4 = new Evento(4L, "Formatura", Date.from(LocalDate.of(2026, 11, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                LocalDateTime.of(2026, 11, 1, 21, 0),
                LocalDateTime.of(2026, 11, 2, 3, 0),
                200, StatusENUM.CANCELADO, enderecoPadrao, clientePadrao);

        List<Orcamento> orcamentos = Arrays.asList(orcamento1, orcamento2, orcamento3, orcamento4);
        List<Evento> eventos = Arrays.asList(evento1, evento2, evento3, evento4);

        when(orcamentoRepository.findAll()).thenReturn(orcamentos);
        when(eventoRepository.findAll()).thenReturn(eventos);

        PainelResumoDto resumo = dashboardService.gerarResumoComercial(null, null);

        assertNotNull(resumo);
        assertEquals(4L, resumo.getTotalOrcamentos());
        assertEquals(2L, resumo.getOrcamentosAprovados());
        assertEquals(1L, resumo.getOrcamentosRecusados());
        assertEquals(1L, resumo.getOrcamentosRascunho());
        assertEquals(50.0, resumo.getTaxaConversao()); // 2 aprovados de 4 total = 50%
        assertEquals(new BigDecimal("15000.00"), resumo.getFaturamentoTotal()); // 10000 + 5000
        assertEquals(new BigDecimal("7500.00"), resumo.getTicketMedio()); // 15000 / 2

        assertEquals(4L, resumo.getTotalEventos());
        assertEquals(1L, resumo.getEventosConfirmados());
        assertEquals(1L, resumo.getEventosPendentes());
        assertEquals(1L, resumo.getEventosFinalizados());
        assertEquals(1L, resumo.getEventosCancelados());
        assertNull(resumo.getPeriodoInicio());
        assertNull(resumo.getPeriodoFim());

        verify(orcamentoRepository, times(1)).findAll();
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Gerar Resumo Comercial - Com período especificado")
    void gerarResumoComercial_ComPeriodo_Sucesso() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);

        List<Orcamento> orcamentos = Arrays.asList(orcamento1, orcamento2);
        List<Evento> eventos = Arrays.asList(evento1, evento2);

        when(orcamentoRepository.findOrcamentosFiltrados(any(Date.class), any(Date.class), eq(inicio), eq(fim)))
                .thenReturn(orcamentos);
        when(eventoRepository.findByDataEventoBetween(any(Date.class), any(Date.class)))
                .thenReturn(eventos);

        PainelResumoDto resumo = dashboardService.gerarResumoComercial(inicio, fim);

        assertNotNull(resumo);
        assertEquals(2L, resumo.getTotalOrcamentos());
        assertEquals(2L, resumo.getOrcamentosAprovados());
        assertEquals(100.0, resumo.getTaxaConversao());
        assertEquals(new BigDecimal("15000.00"), resumo.getFaturamentoTotal());
        assertEquals(new BigDecimal("7500.00"), resumo.getTicketMedio());
        assertEquals(2L, resumo.getTotalEventos());
        assertEquals(1L, resumo.getEventosConfirmados());
        assertEquals(1L, resumo.getEventosPendentes());
        assertEquals(inicio, resumo.getPeriodoInicio());
        assertEquals(fim, resumo.getPeriodoFim());
    }

    @Test
    @DisplayName("Gerar Resumo Comercial - Base Vazia")
    void gerarResumoComercial_BaseVazia() {
        when(orcamentoRepository.findAll()).thenReturn(Collections.emptyList());
        when(eventoRepository.findAll()).thenReturn(Collections.emptyList());

        PainelResumoDto resumo = dashboardService.gerarResumoComercial(null, null);

        assertNotNull(resumo);
        assertEquals(0L, resumo.getTotalOrcamentos());
        assertEquals(0L, resumo.getOrcamentosAprovados());
        assertEquals(0.0, resumo.getTaxaConversao());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resumo.getFaturamentoTotal());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resumo.getTicketMedio());
        assertEquals(0L, resumo.getTotalEventos());
    }

    @Test
    @DisplayName("Gerar Resumo Comercial - Sem Orçamentos Aprovados (Ticket Médio e Faturamento Zero)")
    void gerarResumoComercial_SemAprovados() {
        List<Orcamento> orcamentos = Collections.singletonList(orcamento3); // apenas RECUSADO

        when(orcamentoRepository.findAll()).thenReturn(orcamentos);
        when(eventoRepository.findAll()).thenReturn(Collections.emptyList());

        PainelResumoDto resumo = dashboardService.gerarResumoComercial(null, null);

        assertNotNull(resumo);
        assertEquals(1L, resumo.getTotalOrcamentos());
        assertEquals(0L, resumo.getOrcamentosAprovados());
        assertEquals(1L, resumo.getOrcamentosRecusados());
        assertEquals(0.0, resumo.getTaxaConversao());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resumo.getFaturamentoTotal());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resumo.getTicketMedio());
    }

    @Test
    @DisplayName("Gerar Resumo Comercial - Erro quando data de início é posterior à data de fim")
    void gerarResumoComercial_Erro_DataInicioPosterior() {
        LocalDate inicio = LocalDate.of(2026, 10, 31);
        LocalDate fim = LocalDate.of(2026, 10, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dashboardService.gerarResumoComercial(inicio, fim));
        assertEquals("A data inicial não pode ser posterior à data final.", ex.getMessage());
    }

    @Test
    @DisplayName("Obter Agenda - Sem filtros (Todos os eventos ordenados cronologicamente)")
    void obterAgenda_SemFiltros_Sucesso() {
        List<Evento> eventos = Arrays.asList(evento3, evento1, evento2); // Desordenados propositalmente

        when(eventoRepository.findAll()).thenReturn(eventos);

        AgendaResponseDto response = dashboardService.obterAgenda(null, null, null);

        assertNotNull(response);
        assertEquals(3L, response.getTotalEventosNoPeriodo());
        assertEquals(1L, response.getTotalConfirmadosNoPeriodo());
        assertNull(response.getPeriodoInicio());
        assertNull(response.getPeriodoFim());

        List<AgendaEventoDto> lista = response.getEventos();
        assertEquals(3, lista.size());
        // Deve estar ordenado: evento1 (15/10), evento2 (20/10), evento3 (25/10)
        assertEquals(1L, lista.get(0).getId());
        assertEquals("Casamento Real", lista.get(0).getNomeEvento());
        assertEquals("Lucas Eventos", lista.get(0).getClienteNome());
        assertEquals(LocalDate.of(2026, 10, 15), lista.get(0).getDataEvento());
        assertEquals(LocalTime.of(18, 0), lista.get(0).getHorarioInicio());
        assertEquals(LocalTime.of(23, 0), lista.get(0).getHorarioFim());
        assertEquals("Av. Paulista, 1000 - São Paulo", lista.get(0).getLocalEvento());
        assertEquals(150, lista.get(0).getQtdConvidados());
        assertEquals(StatusENUM.CONFIRMADO, lista.get(0).getStatus());

        assertEquals(2L, lista.get(1).getId());
        assertEquals(3L, lista.get(2).getId());
    }

    @Test
    @DisplayName("Obter Agenda - Com filtro de status e período")
    void obterAgenda_ComFiltros_Sucesso() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);

        when(eventoRepository.findByDataEventoBetweenAndStatus(any(Date.class), any(Date.class), eq(StatusENUM.CONFIRMADO)))
                .thenReturn(Collections.singletonList(evento1));

        AgendaResponseDto response = dashboardService.obterAgenda(inicio, fim, StatusENUM.CONFIRMADO);

        assertNotNull(response);
        assertEquals(1L, response.getTotalEventosNoPeriodo());
        assertEquals(1L, response.getTotalConfirmadosNoPeriodo());
        assertEquals(inicio, response.getPeriodoInicio());
        assertEquals(fim, response.getPeriodoFim());
        assertEquals(1, response.getEventos().size());
        assertEquals("Casamento Real", response.getEventos().get(0).getNomeEvento());
    }

    @Test
    @DisplayName("Obter Agenda - Evento com campos nulos (endereço nulo e cliente nulo)")
    void obterAgenda_CamposNulos() {
        Evento eventoSemDados = new Evento();
        eventoSemDados.setId(99L);
        eventoSemDados.setNome("Evento Simples");
        eventoSemDados.setStatus(StatusENUM.PENDENTE);

        when(eventoRepository.findAll())
                .thenReturn(Collections.singletonList(eventoSemDados));

        AgendaResponseDto response = dashboardService.obterAgenda(null, null, null);

        assertNotNull(response);
        assertEquals(1L, response.getTotalEventosNoPeriodo());
        assertEquals(0L, response.getTotalConfirmadosNoPeriodo());

        AgendaEventoDto dto = response.getEventos().get(0);
        assertEquals(99L, dto.getId());
        assertEquals("Evento Simples", dto.getNomeEvento());
        assertNull(dto.getClienteNome());
        assertNull(dto.getClienteTelefone());
        assertNull(dto.getLocalEvento());
        assertNull(dto.getDataEvento());
    }

    @Test
    @DisplayName("Obter Agenda - Erro quando data de início é posterior à data de fim")
    void obterAgenda_Erro_DataInicioPosterior() {
        LocalDate inicio = LocalDate.of(2026, 12, 31);
        LocalDate fim = LocalDate.of(2026, 1, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dashboardService.obterAgenda(inicio, fim, null));
        assertEquals("A data inicial não pode ser posterior à data final.", ex.getMessage());
    }
}
