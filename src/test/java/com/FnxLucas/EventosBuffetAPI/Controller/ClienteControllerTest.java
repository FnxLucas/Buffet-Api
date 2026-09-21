package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import com.FnxLucas.EventosBuffetAPI.Model.Endereco;
import com.FnxLucas.EventosBuffetAPI.Repository.ClienteRepository;
import com.FnxLucas.EventosBuffetAPI.Service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClienteControllerTest {

    @Autowired
    private ClienteController clienteController;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private ClienteService clienteService;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        Endereco endereco = new Endereco("São Paulo", "Rua A", "100");
        clienteValido = new Cliente(1L, "João Silva", "12345678901", "joao@email.com", endereco);
    }

    @Test
    @DisplayName("POST /clientes - Sucesso (201 Created)")
    void criarCliente_Sucesso() {
        doNothing().when(clienteService).validarCadastro(any(Cliente.class));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        ResponseEntity<?> response = clienteController.criar(clienteValido);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(clienteValido, response.getBody());
    }

    @Test
    @DisplayName("POST /clientes - Erro Validação (400 Bad Request)")
    void criarCliente_DadosInvalidos() {
        doThrow(new IllegalArgumentException("Erro: Nome não preenchido"))
                .when(clienteService).validarCadastro(any(Cliente.class));

        ResponseEntity<?> response = clienteController.criar(new Cliente());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro: Nome não preenchido", response.getBody());
    }

    @Test
    @DisplayName("GET /clientes - Sucesso (200 OK)")
    void listarClientes_Sucesso() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteValido));

        ResponseEntity<List<Cliente>> response = clienteController.listaCliente();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("GET /clientes/{id} - Sucesso (200 OK)")
    void buscarClientePorId_Sucesso() {
        doNothing().when(clienteService).validarClienteId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        ResponseEntity<?> response = clienteController.listarClienteId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteValido, response.getBody());
    }

    @Test
    @DisplayName("GET /clientes/{id} - Não Encontrado (404 Not Found)")
    void buscarClientePorId_NaoEncontrado() {
        doThrow(new IllegalArgumentException("Erro: Cliente não encontrado"))
                .when(clienteService).validarClienteId(99L);

        ResponseEntity<?> response = clienteController.listarClienteId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Erro: Cliente não encontrado", response.getBody());
    }

    @Test
    @DisplayName("PUT /clientes/{id} - Sucesso (200 OK)")
    void atualizarCliente_Sucesso() {
        doNothing().when(clienteService).validarClienteId(1L);
        doNothing().when(clienteService).validarCadastro(any(Cliente.class));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);

        ResponseEntity<?> response = clienteController.atualizarCliente(1L, clienteValido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteValido, response.getBody());
    }

    @Test
    @DisplayName("PUT /clientes/{id} - Dados Inválidos (400 Bad Request)")
    void atualizarCliente_DadosInvalidos() {
        doNothing().when(clienteService).validarClienteId(1L);
        doThrow(new IllegalArgumentException("Erro: CPF ou CNPJ inválido"))
                .when(clienteService).validarCadastro(any(Cliente.class));

        ResponseEntity<?> response = clienteController.atualizarCliente(1L, clienteValido);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro: CPF ou CNPJ inválido", response.getBody());
    }

    @Test
    @DisplayName("DELETE /clientes/{id} - Sucesso (204 No Content)")
    void deletarCliente_Sucesso() {
        doNothing().when(clienteService).validarClienteId(1L);
        doNothing().when(clienteRepository).deleteById(1L);

        ResponseEntity<?> response = clienteController.deletarClienteId(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /clientes/{id} - Não Encontrado (404 Not Found)")
    void deletarCliente_NaoEncontrado() {
        doThrow(new IllegalArgumentException("Erro: Cliente não encontrado"))
                .when(clienteService).validarClienteId(99L);

        ResponseEntity<?> response = clienteController.deletarClienteId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Erro: Cliente não encontrado", response.getBody());
    }
}
