package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Item;
import com.FnxLucas.EventosBuffetAPI.Repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @MockitoBean
    private ItemRepository itemRepository;

    private Item itemValido;

    @BeforeEach
    void setUp() {
        itemValido = new Item(1L, "Decoracao Floral", "Decoração", 1, new BigDecimal("500.00"), new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("POST /itens - Sucesso (201 Created)")
    void criarItem_Sucesso() {
        when(itemRepository.save(any(Item.class))).thenReturn(itemValido);

        ResponseEntity<?> response = itemController.criar(itemValido);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(itemValido, response.getBody());
    }

    @Test
    @DisplayName("POST /itens - Erro Nome Obrigatório (400 Bad Request)")
    void criarItem_NomeObrigatorio() {
        Item itemSemNome = new Item();
        itemSemNome.setNomeItem("   ");

        ResponseEntity<?> response = itemController.criar(itemSemNome);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O nome do item é obrigatório.", response.getBody());
    }

    @Test
    @DisplayName("GET /itens - Sucesso (200 OK)")
    void listarItens_Sucesso() {
        when(itemRepository.findAll()).thenReturn(List.of(itemValido));

        ResponseEntity<List<Item>> response = itemController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /itens/{id} - Sucesso (200 OK)")
    void buscarItemPorId_Sucesso() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemValido));

        ResponseEntity<?> response = itemController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemValido, response.getBody());
    }

    @Test
    @DisplayName("GET /itens/{id} - Não Encontrado (404 Not Found)")
    void buscarItemPorId_NaoEncontrado() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = itemController.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item não encontrado", response.getBody());
    }

    @Test
    @DisplayName("PUT /itens/{id} - Sucesso (200 OK)")
    void atualizarItem_Sucesso() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemValido));
        when(itemRepository.save(any(Item.class))).thenReturn(itemValido);

        ResponseEntity<?> response = itemController.atualizar(1L, itemValido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemValido, response.getBody());
    }

    @Test
    @DisplayName("PUT /itens/{id} - Não Encontrado (404 Not Found)")
    void atualizarItem_NaoEncontrado() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = itemController.atualizar(99L, itemValido);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item não encontrado", response.getBody());
    }

    @Test
    @DisplayName("DELETE /itens/{id} - Sucesso (204 No Content)")
    void deletarItem_Sucesso() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);

        ResponseEntity<?> response = itemController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /itens/{id} - Não Encontrado (404 Not Found)")
    void deletarItem_NaoEncontrado() {
        when(itemRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<?> response = itemController.deletar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item não encontrado", response.getBody());
    }
}
