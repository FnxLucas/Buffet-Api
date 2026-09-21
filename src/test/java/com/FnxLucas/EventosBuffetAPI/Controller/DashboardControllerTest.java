package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Dto.AgendaEventoDto;
import com.FnxLucas.EventosBuffetAPI.Dto.AgendaResponseDto;
import com.FnxLucas.EventosBuffetAPI.Dto.PainelResumoDto;
import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;
import com.FnxLucas.EventosBuffetAPI.Security.JwtTokenProvider;
import com.FnxLucas.EventosBuffetAPI.Service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class DashboardControllerTest {

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private DashboardService dashboardService;

    private MockMvc mockMvc;
    private String validToken;
    private PainelResumoDto resumoMock;
    private AgendaResponseDto agendaMock;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        validToken = jwtTokenProvider.generateToken("admin", "ROLE_ADMIN", "Administrador do Sistema");

        resumoMock = new PainelResumoDto(
                10, 6, 2, 2,
                60.0,
                new BigDecimal("30000.00"),
                new BigDecimal("5000.00"),
                8, 5, 2, 1, 0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        AgendaEventoDto eventoDto = new AgendaEventoDto(
                1L, "Casamento Real", "Maria Silva", null,
                LocalDate.of(2026, 1, 15),
                LocalTime.of(18, 0), LocalTime.of(23, 0),
                "Rua das Flores, 123 - São Paulo", 150,
                StatusENUM.CONFIRMADO
        );

        agendaMock = new AgendaResponseDto(
                List.of(eventoDto),
                1,
                1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
    }

    // =========================================================================
    // 1. Testes Unitários Diretos do Controller
    // =========================================================================

    @Test
    @DisplayName("GET /dashboard/resumo - Sucesso sem parâmetros de data")
    void obterResumoComercial_SemParametros_Sucesso() {
        when(dashboardService.gerarResumoComercial(null, null)).thenReturn(resumoMock);

        ResponseEntity<?> response = dashboardController.obterResumoComercial(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PainelResumoDto);

        PainelResumoDto body = (PainelResumoDto) response.getBody();
        assertEquals(10, body.getTotalOrcamentos());
        assertEquals(6, body.getOrcamentosAprovados());
        assertEquals(new BigDecimal("30000.00"), body.getFaturamentoTotal());
        verify(dashboardService, times(1)).gerarResumoComercial(null, null);
    }

    @Test
    @DisplayName("GET /dashboard/resumo - Sucesso com intervalo de datas válido")
    void obterResumoComercial_ComDatas_Sucesso() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        when(dashboardService.gerarResumoComercial(inicio, fim)).thenReturn(resumoMock);

        ResponseEntity<?> response = dashboardController.obterResumoComercial(inicio, fim);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resumoMock, response.getBody());
        verify(dashboardService, times(1)).gerarResumoComercial(inicio, fim);
    }

    @Test
    @DisplayName("GET /dashboard/resumo - Erro quando dataInicio é posterior a dataFim (400 Bad Request)")
    void obterResumoComercial_DataInvalida_Retorna400() {
        LocalDate inicio = LocalDate.of(2026, 2, 1);
        LocalDate fim = LocalDate.of(2026, 1, 1);
        when(dashboardService.gerarResumoComercial(inicio, fim))
                .thenThrow(new IllegalArgumentException("A data inicial não pode ser posterior à data final."));

        ResponseEntity<?> response = dashboardController.obterResumoComercial(inicio, fim);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("A data inicial não pode ser posterior à data final.", response.getBody());
        verify(dashboardService, times(1)).gerarResumoComercial(inicio, fim);
    }

    @Test
    @DisplayName("GET /dashboard/agenda - Sucesso sem parâmetros de filtro")
    void obterAgenda_SemFiltros_Sucesso() {
        when(dashboardService.obterAgenda(null, null, null)).thenReturn(agendaMock);

        ResponseEntity<?> response = dashboardController.obterAgenda(null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AgendaResponseDto);

        AgendaResponseDto body = (AgendaResponseDto) response.getBody();
        assertEquals(1, body.getTotalEventosNoPeriodo());
        assertEquals(1, body.getTotalConfirmadosNoPeriodo());
        assertEquals(1, body.getEventos().size());
        verify(dashboardService, times(1)).obterAgenda(null, null, null);
    }

    @Test
    @DisplayName("GET /dashboard/agenda - Sucesso com datas e status")
    void obterAgenda_ComDatasEStatus_Sucesso() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        StatusENUM status = StatusENUM.CONFIRMADO;
        when(dashboardService.obterAgenda(inicio, fim, status)).thenReturn(agendaMock);

        ResponseEntity<?> response = dashboardController.obterAgenda(inicio, fim, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(agendaMock, response.getBody());
        verify(dashboardService, times(1)).obterAgenda(inicio, fim, status);
    }

    @Test
    @DisplayName("GET /dashboard/agenda - Erro quando dataInicio é posterior a dataFim (400 Bad Request)")
    void obterAgenda_DataInvalida_Retorna400() {
        LocalDate inicio = LocalDate.of(2026, 2, 1);
        LocalDate fim = LocalDate.of(2026, 1, 1);
        when(dashboardService.obterAgenda(inicio, fim, null))
                .thenThrow(new IllegalArgumentException("A data inicial não pode ser posterior à data final."));

        ResponseEntity<?> response = dashboardController.obterAgenda(inicio, fim, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("A data inicial não pode ser posterior à data final.", response.getBody());
        verify(dashboardService, times(1)).obterAgenda(inicio, fim, null);
    }

    // =========================================================================
    // 2. Testes MockMvc com autenticação e validação de serialização HTTP
    // =========================================================================

    @Test
    @DisplayName("MockMvc GET /dashboard/resumo - Com Token Válido Retorna JSON 200 com campos calculados")
    void mockMvc_obterResumoComercial_ComToken_Retorna200() throws Exception {
        when(dashboardService.gerarResumoComercial(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(resumoMock);

        mockMvc.perform(get("/dashboard/resumo")
                        .header("Authorization", "Bearer " + validToken)
                        .param("dataInicio", "2026-01-01")
                        .param("dataFim", "2026-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrcamentos").value(10))
                .andExpect(jsonPath("$.orcamentosAprovados").value(6))
                .andExpect(jsonPath("$.orcamentosRecusados").value(2))
                .andExpect(jsonPath("$.orcamentosRascunho").value(2))
                .andExpect(jsonPath("$.taxaConversao").value(60.0))
                .andExpect(jsonPath("$.faturamentoTotal").value(30000.00))
                .andExpect(jsonPath("$.ticketMedio").value(5000.00))
                .andExpect(jsonPath("$.totalEventos").value(8))
                .andExpect(jsonPath("$.eventosConfirmados").value(5))
                .andExpect(jsonPath("$.periodoInicio").value("2026-01-01"))
                .andExpect(jsonPath("$.periodoFim").value("2026-01-31"));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/agenda - Com Token Válido Retorna JSON 200 com lista ordenada e contadores")
    void mockMvc_obterAgenda_ComToken_Retorna200() throws Exception {
        when(dashboardService.obterAgenda(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), StatusENUM.CONFIRMADO))
                .thenReturn(agendaMock);

        mockMvc.perform(get("/dashboard/agenda")
                        .header("Authorization", "Bearer " + validToken)
                        .param("dataInicio", "2026-01-01")
                        .param("dataFim", "2026-01-31")
                        .param("status", "CONFIRMADO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEventosNoPeriodo").value(1))
                .andExpect(jsonPath("$.totalConfirmadosNoPeriodo").value(1))
                .andExpect(jsonPath("$.eventos[0].id").value(1))
                .andExpect(jsonPath("$.eventos[0].nomeEvento").value("Casamento Real"))
                .andExpect(jsonPath("$.eventos[0].clienteNome").value("Maria Silva"))
                .andExpect(jsonPath("$.eventos[0].dataEvento").value("2026-01-15"))
                .andExpect(jsonPath("$.eventos[0].horarioInicio").value("18:00:00"))
                .andExpect(jsonPath("$.eventos[0].localEvento").value("Rua das Flores, 123 - São Paulo"))
                .andExpect(jsonPath("$.eventos[0].status").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/resumo - Com Token Válido sem parâmetros retorna 200 OK")
    void mockMvc_obterResumoComercial_SemParametros_Retorna200() throws Exception {
        when(dashboardService.gerarResumoComercial(null, null)).thenReturn(resumoMock);

        mockMvc.perform(get("/dashboard/resumo")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrcamentos").value(10))
                .andExpect(jsonPath("$.orcamentosAprovados").value(6))
                .andExpect(jsonPath("$.taxaConversao").value(60.0))
                .andExpect(jsonPath("$.faturamentoTotal").value(30000.00))
                .andExpect(jsonPath("$.ticketMedio").value(5000.00));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/agenda - Com Token Válido sem parâmetros retorna 200 OK")
    void mockMvc_obterAgenda_SemParametros_Retorna200() throws Exception {
        when(dashboardService.obterAgenda(null, null, null)).thenReturn(agendaMock);

        mockMvc.perform(get("/dashboard/agenda")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEventosNoPeriodo").value(1))
                .andExpect(jsonPath("$.eventos[0].nomeEvento").value("Casamento Real"));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/resumo - Retorna 400 Bad Request para dataInicio posterior a dataFim")
    void mockMvc_obterResumoComercial_DataInvalida_Retorna400() throws Exception {
        when(dashboardService.gerarResumoComercial(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .thenThrow(new IllegalArgumentException("A data inicial não pode ser posterior à data final."));

        mockMvc.perform(get("/dashboard/resumo")
                        .header("Authorization", "Bearer " + validToken)
                        .param("dataInicio", "2026-02-01")
                        .param("dataFim", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A data inicial não pode ser posterior à data final."));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/agenda - Retorna 400 Bad Request para dataInicio posterior a dataFim")
    void mockMvc_obterAgenda_DataInvalida_Retorna400() throws Exception {
        when(dashboardService.obterAgenda(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1), null))
                .thenThrow(new IllegalArgumentException("A data inicial não pode ser posterior à data final."));

        mockMvc.perform(get("/dashboard/agenda")
                        .header("Authorization", "Bearer " + validToken)
                        .param("dataInicio", "2026-02-01")
                        .param("dataFim", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A data inicial não pode ser posterior à data final."));
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/resumo - Sem autenticação retorna 401 Unauthorized")
    void mockMvc_obterResumoComercial_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/dashboard/resumo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("MockMvc GET /dashboard/agenda - Sem autenticação retorna 401 Unauthorized")
    void mockMvc_obterAgenda_SemToken_Retorna401() throws Exception {
        mockMvc.perform(get("/dashboard/agenda"))
                .andExpect(status().isUnauthorized());
    }
}
