package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import com.FnxLucas.EventosBuffetAPI.Repository.ClienteRepository;
import com.FnxLucas.EventosBuffetAPI.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Cliente clientRequest) {
        try {
            clienteService.validarCadastro(clientRequest);
            Cliente clienteSalvo = clienteRepository.save(clientRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listaCliente() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarClienteId(@PathVariable("id") Long id) {
        try {
            clienteService.validarClienteId(id);
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable("id") Long id, @RequestBody Cliente clientRequest) {
        try {
            clienteService.validarClienteId(id);
            clienteService.validarCadastro(clientRequest);

            Cliente clienteExistente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

            clienteExistente.setNome(clientRequest.getNome());
            clienteExistente.setCpfCnpj(clientRequest.getCpfCnpj());
            clienteExistente.setEmail(clientRequest.getEmail());
            clienteExistente.setEndereco(clientRequest.getEndereco());

            return ResponseEntity.ok(clienteRepository.save(clienteExistente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarClienteId(@PathVariable("id") Long id) {
        try {
            clienteService.validarClienteId(id);
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

