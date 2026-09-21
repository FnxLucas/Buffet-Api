package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Orcamento;
import com.FnxLucas.EventosBuffetAPI.Service.EventoService;
import com.FnxLucas.EventosBuffetAPI.Service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Orcamento orcamentoRequest) {
        try {
            Orcamento orcamentoSalvo = orcamentoService.salvarOrcamento(orcamentoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Orcamento>> listar() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return orcamentoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orçamento não encontrado"));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> buscarPorEvento(@PathVariable Long eventoId) {
        try {
            eventoService.validarEventoId(eventoId);
            List<Orcamento> orcamentos = orcamentoService.buscarPorEvento(eventoId);
            return ResponseEntity.ok(orcamentos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Orcamento orcamentoRequest) {
        try {
            Orcamento orcamentoAtualizado = orcamentoService.atualizarOrcamento(id, orcamentoRequest);
            return ResponseEntity.ok(orcamentoAtualizado);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Orçamento não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        try {
            Orcamento orcamentoAprovado = orcamentoService.aprovarOrcamento(id);
            return ResponseEntity.ok(orcamentoAprovado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id) {
        try {
            Orcamento orcamentoRecusado = orcamentoService.recusarOrcamento(id);
            return ResponseEntity.ok(orcamentoRecusado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/download-docx")
    public ResponseEntity<?> downloadDocx(@PathVariable Long id) {
        try {
            byte[] bytes = orcamentoService.gerarPropostaDocx(id);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Proposta_Orcamento_" + id + ".docx\"")
                    .body(bytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            orcamentoService.deletarOrcamento(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
