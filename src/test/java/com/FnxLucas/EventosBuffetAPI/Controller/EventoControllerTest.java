package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import com.FnxLucas.EventosBuffetAPI.Model.Endereco;
import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;
import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import com.FnxLucas.EventosBuffetAPI.Service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EventoControllerTest {

    @Autowired
    private EventoController eventoController;

    @MockitoBean
    private EventoRepository eventoRepository;

    @MockitoBean
    private EventoService eventoService;

    private Evento eventoValido;

    @BeforeEach
    void setUp() {
        Endereco endereco = new Endereco("São Paulo", "Av Paulista", "1000");
        Cliente cliente = new Cliente(1L, "Maria Souza", "98765432100", "maria@email.com", endereco);
        eventoValido = new Evento(
                1L,
                "Casamento Maria e João",
                new Date(),
                LocalDateTime.of(2026, 12, 10, 18, 0),
                LocalDateTime.of(2026, 12, 11, 0, 0),
                150,
                StatusENUM.CONFIRMADO,
                endereco,
                cliente
        );
    }

    @Test
    @DisplayName("POST /eventos - Sucesso (201 Created)")
    void criarEvento_Sucesso() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoValido);

        ResponseEntity<?> response = eventoController.criar(eventoValido);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(eventoValido, response.getBody());
    }

    @Test
    @DisplayName("POST /eventos - Erro Validação (400 Bad Request)")
    void criarEvento_DadosInvalidos() {
        when(eventoRepository.save(any(Evento.class)))
                .thenThrow(new IllegalArgumentException("Erro ao salvar evento: dados inválidos"));

        ResponseEntity<?> response = eventoController.criar(new Evento());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao salvar evento: dados inválidos", response.getBody());
    }

    @Test
    @DisplayName("GET /eventos - Sucesso (200 OK)")
    void listarEventos_Sucesso() {
        when(eventoRepository.findAll()).thenReturn(List.of(eventoValido));

        ResponseEntity<List<Evento>> response = eventoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Casamento Maria e João", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("GET /eventos/{id} - Sucesso (200 OK)")
    void buscarEventoPorId_Sucesso() {
        doNothing().when(eventoService).validarEventoId(1L);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));

        ResponseEntity<?> response = eventoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventoValido, response.getBody());
    }

    @Test
    @DisplayName("GET /eventos/{id} - Não Encontrado (404 Not Found)")
    void buscarEventoPorId_NaoEncontrado() {
        doThrow(new IllegalArgumentException("Erro: Evento não encontrado"))
                .when(eventoService).validarEventoId(99L);

        ResponseEntity<?> response = eventoController.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Erro: Evento não encontrado", response.getBody());
    }

    @Test
    @DisplayName("PUT /eventos/{id} - Sucesso (200 OK)")
    void atualizarEvento_Sucesso() {
        doNothing().when(eventoService).validarEventoId(1L);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoValido);

        ResponseEntity<?> response = eventoController.atualizar(1L, eventoValido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventoValido, response.getBody());
    }

    @Test
    @DisplayName("PUT /eventos/{id} - Dados Inválidos (400 Bad Request)")
    void atualizarEvento_DadosInvalidos() {
        doNothing().when(eventoService).validarEventoId(1L);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoValido));
        when(eventoRepository.save(any(Evento.class)))
                .thenThrow(new IllegalArgumentException("Erro na atualização do evento"));

        ResponseEntity<?> response = eventoController.atualizar(1L, eventoValido);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro na atualização do evento", response.getBody());
    }

    @Test
    @DisplayName("DELETE /eventos/{id} - Sucesso (204 No Content)")
    void deletarEvento_Sucesso() {
        doNothing().when(eventoService).validarEventoId(1L);
        doNothing().when(eventoRepository).deleteById(1L);

        ResponseEntity<?> response = eventoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /eventos/{id} - Não Encontrado (404 Not Found)")
    void deletarEvento_NaoEncontrado() {
        doThrow(new IllegalArgumentException("Erro: Evento não encontrado"))
                .when(eventoService).validarEventoId(99L);

        ResponseEntity<?> response = eventoController.deletar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Erro: Evento não encontrado", response.getBody());
    }
}
