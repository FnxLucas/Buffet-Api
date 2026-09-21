package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Model.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocxGeneratorServiceTest {

    private DocxGeneratorService docxGeneratorService;

    @BeforeEach
    void setUp() {
        docxGeneratorService = new DocxGeneratorService();
    }

    @Test
    @DisplayName("Deve gerar arquivo DOCX valido com dados completos do orcamento")
    void deveGerarDocxValidoComDadosCompletos() throws IOException {
        Endereco enderecoCliente = new Endereco("São Paulo", "Rua das Flores", "123");
        Cliente cliente = new Cliente(1L, "João Silva", "123.456.789-00", "joao@email.com", enderecoCliente);

        Endereco enderecoEvento = new Endereco("Campinas", "Av. Principal", "500");
        Evento evento = new Evento(10L, "Aniversário de 15 Anos", new Date(), LocalDateTime.now(), LocalDateTime.now().plusHours(4), 100, StatusENUM.PENDENTE, enderecoEvento, cliente);

        Item item1 = new Item(1L, "Buffet Completo", "Alimentação", 100, new BigDecimal("80.00"), new BigDecimal("8000.00"));
        Item item2 = new Item(2L, "Decoração Standard", "Decoração", 1, new BigDecimal("2000.00"), new BigDecimal("2000.00"));
        List<Item> itens = new ArrayList<>();
        itens.add(item1);
        itens.add(item2);

        Orcamento orcamento = new Orcamento(100L, evento, null, "Observação de teste", "50% de entrada", new BigDecimal("10000.00"), new BigDecimal("9500.00"), -5.0, new BigDecimal("500.00"), new BigDecimal("9000.00"), StatusOrcamentoENUM.RASCUNHO, itens);

        byte[] bytes = docxGeneratorService.gerarPropostaDocx(orcamento);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            assertNotNull(doc);
            assertFalse(doc.getParagraphs().isEmpty());
            assertFalse(doc.getTables().isEmpty());
        }
    }

    @Test
    @DisplayName("Deve extrair corretamente o mapa de tags do orcamento")
    void deveExtrairMapaDeTagsCorretamente() {
        Cliente cliente = new Cliente(1L, "Maria Santos", "987.654.321-11", "maria@email.com", new Endereco("Rio de Janeiro", "Rua A", "42"));
        Evento evento = new Evento(5L, "Casamento", new Date(), LocalDateTime.of(2026, 10, 15, 18, 0), LocalDateTime.of(2026, 10, 15, 23, 0), 200, StatusENUM.CONFIRMADO, new Endereco("Niterói", "Estrada B", "100"), cliente);

        Orcamento orcamento = new Orcamento(50L, evento, null, "Sem observações", null, new BigDecimal("15000.00"), null, 0.0, BigDecimal.ZERO, new BigDecimal("15000.00"), StatusOrcamentoENUM.APROVADO, new ArrayList<>());

        Map<String, String> tags = docxGeneratorService.extrairValoresTags(orcamento);

        assertEquals("Maria Santos", tags.get("{{cliente_nome}}"));
        assertEquals("maria@email.com", tags.get("{{cliente_email}}"));
        assertEquals("Casamento", tags.get("{{evento_nome}}"));
        assertEquals("18:00", tags.get("{{horario_inicio}}"));
        assertEquals("23:00", tags.get("{{horario_fim}}"));
        assertEquals("200", tags.get("{{qtd_convidados}}"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar gerar DOCX com orcamento nulo")
    void deveLancarExcecaoComOrcamentoNulo() {
        assertThrows(IllegalArgumentException.class, () -> docxGeneratorService.gerarPropostaDocx(null));
    }
}
