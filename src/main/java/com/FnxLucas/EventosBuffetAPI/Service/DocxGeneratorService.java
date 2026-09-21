package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import com.FnxLucas.EventosBuffetAPI.Model.Endereco;
import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Model.Item;
import com.FnxLucas.EventosBuffetAPI.Model.Orcamento;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DocxGeneratorService {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(LOCALE_PT_BR);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

    public byte[] gerarPropostaDocx(Orcamento orcamento) {
        if (orcamento == null) {
            throw new IllegalArgumentException("O orçamento não pode ser nulo para geração de DOCX");
        }

        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Title
            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("PROPOSTA DE ORÇAMENTO");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("Calibri");

            // Subtitle / ID
            XWPFParagraph subTitlePara = document.createParagraph();
            subTitlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun subTitleRun = subTitlePara.createRun();
            subTitleRun.setText("Orçamento nº: " + (orcamento.getId() != null ? orcamento.getId() : "N/A"));
            subTitleRun.setFontSize(12);
            subTitleRun.setColor("555555");
            subTitleRun.setFontFamily("Calibri");

            document.createParagraph(); // Empty spacing

            // Map values for tag replacement / document construction
            Map<String, String> values = extrairValoresTags(orcamento);

            // 1. Dados do Cliente
            adicionarSecaoTitulo(document, "1. DADOS DO CLIENTE");
            adicionarLinhaCampo(document, "Nome: ", values.get("{{cliente_nome}}"));
            adicionarLinhaCampo(document, "E-mail: ", values.get("{{cliente_email}}"));
            adicionarLinhaCampo(document, "Endereço: ", values.get("{{cliente_endereco}}"));

            document.createParagraph();

            // 2. Dados do Evento
            adicionarSecaoTitulo(document, "2. DADOS DO EVENTO");
            adicionarLinhaCampo(document, "Nome do Evento: ", values.get("{{evento_nome}}"));
            adicionarLinhaCampo(document, "Data do Evento: ", values.get("{{data_evento}}"));
            adicionarLinhaCampo(document, "Horário de Início: ", values.get("{{horario_inicio}}"));
            adicionarLinhaCampo(document, "Horário de Término: ", values.get("{{horario_fim}}"));
            adicionarLinhaCampo(document, "Local do Evento: ", values.get("{{local_evento}}"));
            adicionarLinhaCampo(document, "Quantidade de Convidados: ", values.get("{{qtd_convidados}}"));

            document.createParagraph();

            // 3. Itens do Orçamento
            adicionarSecaoTitulo(document, "3. ITENS DO ORÇAMENTO");
            gerarTabelaItens(document, orcamento.getListaItens());

            document.createParagraph();

            // 4. Resumo Financeiro
            adicionarSecaoTitulo(document, "4. RESUMO FINANCEIRO");
            adicionarLinhaCampo(document, "Subtotal: ", values.get("{{subtotal}}"));
            if (!values.get("{{valor_final_manual}}").isEmpty()) {
                adicionarLinhaCampo(document, "Valor Ajustado Manualmente: ", values.get("{{valor_final_manual}}"));
            }
            adicionarLinhaCampo(document, "Desconto: ", values.get("{{desconto}}"));
            adicionarLinhaCampoDestaque(document, "TOTAL: ", values.get("{{total}}"));

            document.createParagraph();

            // 5. Observações
            if (!values.get("{{observacoes}}").isEmpty()) {
                adicionarSecaoTitulo(document, "5. OBSERVAÇÕES E CONDIÇÕES");
                adicionarLinhaCampo(document, "Observações: ", values.get("{{observacoes}}"));
            }

            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar documento DOCX do orçamento", e);
        }
    }

    public byte[] preencherTemplateDocx(InputStream templateStream, Orcamento orcamento) {
        if (templateStream == null) {
            throw new IllegalArgumentException("InputStream do template não pode ser nulo");
        }
        if (orcamento == null) {
            throw new IllegalArgumentException("O orçamento não pode ser nulo");
        }

        try (XWPFDocument document = new XWPFDocument(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Map<String, String> tagMap = extrairValoresTags(orcamento);

            // Replace in paragraphs
            for (XWPFParagraph p : document.getParagraphs()) {
                substituirTagsEmParagrafo(p, tagMap, orcamento);
            }

            // Replace in tables
            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            substituirTagsEmParagrafo(p, tagMap, orcamento);
                        }
                    }
                }
            }

            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao preencher template DOCX", e);
        }
    }

    public Map<String, String> extrairValoresTags(Orcamento orcamento) {
        Map<String, String> map = new HashMap<>();

        Evento evento = orcamento.getEvento();
        Cliente cliente = evento != null ? evento.getCliente() : null;

        // Cliente tags
        map.put("{{cliente_nome}}", cliente != null && cliente.getNome() != null ? cliente.getNome() : "");
        map.put("{{cliente_email}}", cliente != null && cliente.getEmail() != null ? cliente.getEmail() : "");
        map.put("{{cliente_endereco}}", cliente != null ? formatarEndereco(cliente.getEndereco()) : "");

        // Evento tags
        map.put("{{evento_nome}}", evento != null && evento.getNome() != null ? evento.getNome() : "");
        map.put("{{data_evento}}", evento != null && evento.getDataEvento() != null ? DATE_FORMATTER.format(evento.getDataEvento()) : "");
        map.put("{{horario_inicio}}", evento != null && evento.getHorarioInicio() != null ? evento.getHorarioInicio().format(TIME_FORMATTER) : "");
        map.put("{{horario_fim}}", evento != null && evento.getHorarioFim() != null ? evento.getHorarioFim().format(TIME_FORMATTER) : "");
        map.put("{{local_evento}}", evento != null ? formatarEndereco(evento.getEndereco()) : "");
        map.put("{{qtd_convidados}}", evento != null ? String.valueOf(evento.getQtdConvidados()) : "0");

        // Financeiro tags
        map.put("{{subtotal}}", formatarMoeda(orcamento.getSubtotal()));
        map.put("{{valor_final_manual}}", orcamento.getValorFinalManual() != null ? formatarMoeda(orcamento.getValorFinalManual()) : "");
        map.put("{{desconto}}", formatarMoeda(orcamento.getDesconto()));
        map.put("{{total}}", formatarMoeda(orcamento.getTotal()));
        map.put("{{observacoes}}", orcamento.getObservacoes() != null ? orcamento.getObservacoes() : "");

        return map;
    }

    private void substituirTagsEmParagrafo(XWPFParagraph p, Map<String, String> tagMap, Orcamento orcamento) {
        String texto = p.getText();
        if (texto == null || texto.isEmpty()) return;

        if (texto.contains("{{tabela_itens}}")) {
            for (int i = p.getRuns().size() - 1; i >= 0; i--) {
                p.removeRun(i);
            }
            gerarTabelaItens(p.getDocument(), orcamento.getListaItens());
            return;
        }

        for (Map.Entry<String, String> entry : tagMap.entrySet()) {
            if (texto.contains(entry.getKey())) {
                texto = texto.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
            }
        }

        List<XWPFRun> runs = p.getRuns();
        if (runs != null && !runs.isEmpty()) {
            runs.get(0).setText(texto, 0);
            for (int i = runs.size() - 1; i >= 1; i--) {
                p.removeRun(i);
            }
        } else {
            XWPFRun newRun = p.createRun();
            newRun.setText(texto);
        }
    }

    private void adicionarSecaoTitulo(XWPFDocument doc, String titulo) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(titulo);
        run.setBold(true);
        run.setFontSize(14);
        run.setColor("1A365D");
        run.setFontFamily("Calibri");
    }

    private void adicionarLinhaCampo(XWPFDocument doc, String rotulo, String valor) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(40);
        XWPFRun runRotulo = p.createRun();
        runRotulo.setText(rotulo);
        runRotulo.setBold(true);
        runRotulo.setFontSize(11);
        runRotulo.setFontFamily("Calibri");

        XWPFRun runValor = p.createRun();
        runValor.setText(valor != null ? valor : "");
        runValor.setFontSize(11);
        runValor.setFontFamily("Calibri");
    }

    private void adicionarLinhaCampoDestaque(XWPFDocument doc, String rotulo, String valor) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(60);
        XWPFRun runRotulo = p.createRun();
        runRotulo.setText(rotulo);
        runRotulo.setBold(true);
        runRotulo.setFontSize(13);
        runRotulo.setColor("2B6CB0");
        runRotulo.setFontFamily("Calibri");

        XWPFRun runValor = p.createRun();
        runValor.setText(valor != null ? valor : "");
        runValor.setBold(true);
        runValor.setFontSize(13);
        runValor.setColor("2B6CB0");
        runValor.setFontFamily("Calibri");
    }

    private void gerarTabelaItens(XWPFDocument doc, List<Item> itens) {
        XWPFTable table = doc.createTable();
        table.setWidth("100%");

        // Header Row
        XWPFTableRow headerRow = table.getRow(0);
        setHeaderCell(headerRow.getCell(0), "Nome do Item");
        setHeaderCell(headerRow.createCell(), "Quantidade");
        setHeaderCell(headerRow.createCell(), "Valor Unitário");
        setHeaderCell(headerRow.createCell(), "Valor Total");

        if (itens != null && !itens.isEmpty()) {
            for (Item item : itens) {
                if (item == null) continue;
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(item.getNomeItem() != null ? item.getNomeItem() : "");
                row.getCell(1).setText(item.getQuantidade() != null ? String.valueOf(item.getQuantidade()) : "0");
                row.getCell(2).setText(formatarMoeda(item.getValorUnitario()));
                row.getCell(3).setText(formatarMoeda(item.getValorTotal()));
            }
        } else {
            XWPFTableRow emptyRow = table.createRow();
            emptyRow.getCell(0).setText("Nenhum item cadastrado");
            emptyRow.getCell(1).setText("-");
            emptyRow.getCell(2).setText("-");
            emptyRow.getCell(3).setText("-");
        }
    }

    private void setHeaderCell(XWPFTableCell cell, String text) {
        cell.setColor("E2E8F0");
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontFamily("Calibri");
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return CURRENCY_FORMAT.format(BigDecimal.ZERO);
        }
        return CURRENCY_FORMAT.format(valor);
    }

    private String formatarEndereco(Endereco endereco) {
        if (endereco == null) return "";
        StringBuilder sb = new StringBuilder();
        if (endereco.getRua() != null && !endereco.getRua().isBlank()) {
            sb.append(endereco.getRua());
        }
        if (endereco.getNumero() != null && !endereco.getNumero().isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(endereco.getNumero());
        }
        if (endereco.getCidade() != null && !endereco.getCidade().isBlank()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.getCidade());
        }
        return sb.toString();
    }
}
