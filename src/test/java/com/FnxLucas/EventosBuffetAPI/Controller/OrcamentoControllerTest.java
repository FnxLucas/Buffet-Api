package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.*;
import com.FnxLucas.EventosBuffetAPI.Repository.OrcamentoRepository;
import com.FnxLucas.EventosBuffetAPI.Service.EventoService;
import com.FnxLucas.EventosBuffetAPI.Service.OrcamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrcamentoControllerTest {

    @Autowired
    private OrcamentoController orcamentoController;

    @MockitoBean
    private OrcamentoRepository orcamentoRepository;

    @MockitoBean
    private OrcamentoService orcamentoService;

    @MockitoBean
    private EventoService eventoService;

    private Orcamento orcamentoValido;
    private Evento eventoValido;

    @BeforeEach
    void setUp() {
        eventoValido = new Evento();
        eventoValido.setId(1L);
        eventoValido.setNome("Casamento VIP");

        Item item = new Item(1L, "Salgadinhos", "Alimentação", 100, new BigDecimal("2.50"), new BigDecimal("250.00"));
        orcamentoValido = new Orcamento(
                1L,
                eventoValido,
                LocalDate.now().plusDays(10),
                "Observações teste",
                "Pagamento 50% de entrada",
                new BigDecimal("250.00"),
                null,
                10.0,
                new BigDecimal("10.00"),
                new BigDecimal("240.00"),
                StatusOrcamentoENUM.RASCUNHO,
                List.of(item)
        );
    }

    @Test
    @DisplayName("POST /orcamentos - Sucesso (201 Created)")
    void criarOrcamento_Sucesso() {
        when(orcamentoService.salvarOrcamento(any(Orcamento.class))).thenReturn(orcamentoValido);

        ResponseEntity<?> response = orcamentoController.criar(orcamentoValido);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orcamentoValido, response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos - Erro Validação (400 Bad Request)")
    void criarOrcamento_DadosInvalidos() {
        when(orcamentoService.salvarOrcamento(any(Orcamento.class)))
                .thenThrow(new IllegalArgumentException("Evento é obrigatório para o orçamento."));

        ResponseEntity<?> response = orcamentoController.criar(new Orcamento());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Evento é obrigatório para o orçamento.", response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos - Erro Evento Inexistente (400 Bad Request)")
    void criarOrcamento_EventoInexistente() {
        when(orcamentoService.salvarOrcamento(any(Orcamento.class)))
                .thenThrow(new IllegalArgumentException("Evento não encontrado"));

        ResponseEntity<?> response = orcamentoController.criar(orcamentoValido);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Evento não encontrado", response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos - Sucesso (200 OK)")
    void listarOrcamentos_Sucesso() {
        when(orcamentoService.listarTodos()).thenReturn(List.of(orcamentoValido));

        ResponseEntity<List<Orcamento>> response = orcamentoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /orcamentos/{id} - Sucesso (200 OK)")
    void buscarOrcamentoPorId_Sucesso() {
        when(orcamentoService.buscarPorId(1L)).thenReturn(Optional.of(orcamentoValido));

        ResponseEntity<?> response = orcamentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orcamentoValido, response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos/{id} - Não Encontrado (404 Not Found)")
    void buscarOrcamentoPorId_NaoEncontrado() {
        when(orcamentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = orcamentoController.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado", response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos/evento/{eventoId} - Sucesso (200 OK)")
    void buscarOrcamentoPorEvento_Sucesso() {
        doNothing().when(eventoService).validarEventoId(1L);
        when(orcamentoService.buscarPorEvento(1L)).thenReturn(List.of(orcamentoValido));

        ResponseEntity<?> response = orcamentoController.buscarPorEvento(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos/evento/{eventoId} - Não Encontrado (404 Not Found)")
    void buscarOrcamentoPorEvento_EventoNaoEncontrado() {
        doThrow(new IllegalArgumentException("Evento não encontrado"))
                .when(eventoService).validarEventoId(99L);

        ResponseEntity<?> response = orcamentoController.buscarPorEvento(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Evento não encontrado", response.getBody());
    }

    @Test
    @DisplayName("PUT /orcamentos/{id} - Sucesso (200 OK)")
    void atualizarOrcamento_Sucesso() {
        when(orcamentoService.atualizarOrcamento(eq(1L), any(Orcamento.class))).thenReturn(orcamentoValido);

        ResponseEntity<?> response = orcamentoController.atualizar(1L, orcamentoValido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orcamentoValido, response.getBody());
    }

    @Test
    @DisplayName("PUT /orcamentos/{id} - Não Encontrado (404 Not Found)")
    void atualizarOrcamento_NaoEncontrado() {
        when(orcamentoService.atualizarOrcamento(eq(99L), any(Orcamento.class)))
                .thenThrow(new IllegalArgumentException("Orçamento não encontrado com ID: 99"));

        ResponseEntity<?> response = orcamentoController.atualizar(99L, orcamentoValido);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado com ID: 99", response.getBody());
    }

    @Test
    @DisplayName("PUT /orcamentos/{id} - Dados Inválidos (400 Bad Request)")
    void atualizarOrcamento_DadosInvalidos() {
        when(orcamentoService.atualizarOrcamento(eq(1L), any(Orcamento.class)))
                .thenThrow(new IllegalArgumentException("Validade inválida"));

        ResponseEntity<?> response = orcamentoController.atualizar(1L, orcamentoValido);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validade inválida", response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos/{id}/aprovar - Sucesso (200 OK)")
    void aprovarOrcamento_Sucesso() {
        orcamentoValido.setStatus(StatusOrcamentoENUM.APROVADO);
        when(orcamentoService.aprovarOrcamento(1L)).thenReturn(orcamentoValido);

        ResponseEntity<?> response = orcamentoController.aprovar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orcamentoValido, response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos/{id}/aprovar - Não Encontrado (404 Not Found)")
    void aprovarOrcamento_NaoEncontrado() {
        when(orcamentoService.aprovarOrcamento(99L))
                .thenThrow(new IllegalArgumentException("Orçamento não encontrado com ID: 99"));

        ResponseEntity<?> response = orcamentoController.aprovar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado com ID: 99", response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos/{id}/recusar - Sucesso (200 OK)")
    void recusarOrcamento_Sucesso() {
        orcamentoValido.setStatus(StatusOrcamentoENUM.RECUSADO);
        when(orcamentoService.recusarOrcamento(1L)).thenReturn(orcamentoValido);

        ResponseEntity<?> response = orcamentoController.recusar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orcamentoValido, response.getBody());
    }

    @Test
    @DisplayName("POST /orcamentos/{id}/recusar - Não Encontrado (404 Not Found)")
    void recusarOrcamento_NaoEncontrado() {
        when(orcamentoService.recusarOrcamento(99L))
                .thenThrow(new IllegalArgumentException("Orçamento não encontrado com ID: 99"));

        ResponseEntity<?> response = orcamentoController.recusar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado com ID: 99", response.getBody());
    }

    @Test
    @DisplayName("DELETE /orcamentos/{id} - Sucesso (204 No Content)")
    void deletarOrcamento_Sucesso() {
        doNothing().when(orcamentoService).deletarOrcamento(1L);

        ResponseEntity<?> response = orcamentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /orcamentos/{id} - Não Encontrado (404 Not Found)")
    void deletarOrcamento_NaoEncontrado() {
        doThrow(new IllegalArgumentException("Orçamento não encontrado com ID: 99"))
                .when(orcamentoService).deletarOrcamento(99L);

        ResponseEntity<?> response = orcamentoController.deletar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado com ID: 99", response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos/{id}/download-docx - Sucesso (200 OK)")
    void downloadDocx_Sucesso() {
        byte[] fakeBytes = "PK\3\4fake docx content".getBytes();
        when(orcamentoService.gerarPropostaDocx(1L)).thenReturn(fakeBytes);

        ResponseEntity<?> response = orcamentoController.downloadDocx(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                response.getHeaders().getFirst(org.springframework.http.HttpHeaders.CONTENT_TYPE));
        assertEquals("attachment; filename=\"Proposta_Orcamento_1.docx\"",
                response.getHeaders().getFirst(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getBody());
        assertArrayEquals(fakeBytes, (byte[]) response.getBody());
    }

    @Test
    @DisplayName("GET /orcamentos/{id}/download-docx - Não Encontrado (404 Not Found)")
    void downloadDocx_NaoEncontrado() {
        when(orcamentoService.gerarPropostaDocx(99L))
                .thenThrow(new IllegalArgumentException("Orçamento não encontrado com ID: 99"));

        ResponseEntity<?> response = orcamentoController.downloadDocx(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orçamento não encontrado com ID: 99", response.getBody());
    }
}

