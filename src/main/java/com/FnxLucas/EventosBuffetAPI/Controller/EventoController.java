package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import com.FnxLucas.EventosBuffetAPI.Service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Evento eventoRequest) {
        try {
            Evento eventoSalvo = eventoRepository.save(eventoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.ok(eventoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            eventoService.validarEventoId(id);
            Evento evento = eventoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
            return ResponseEntity.ok(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Evento eventoRequest) {
        try {
            eventoService.validarEventoId(id);
            Evento eventoExistente = eventoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

            eventoExistente.setNome(eventoRequest.getNome());
            eventoExistente.setDataEvento(eventoRequest.getDataEvento());
            eventoExistente.setHorarioInicio(eventoRequest.getHorarioInicio());
            eventoExistente.setHorarioFim(eventoRequest.getHorarioFim());
            eventoExistente.setQtdConvidados(eventoRequest.getQtdConvidados());
            eventoExistente.setStatus(eventoRequest.getStatus());
            eventoExistente.setEndereco(eventoRequest.getEndereco());
            eventoExistente.setCliente(eventoRequest.getCliente());

            return ResponseEntity.ok(eventoRepository.save(eventoExistente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            eventoService.validarEventoId(id);
            eventoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

