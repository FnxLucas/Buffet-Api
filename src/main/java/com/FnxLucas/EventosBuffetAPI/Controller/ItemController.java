package com.FnxLucas.EventosBuffetAPI.Controller;

import com.FnxLucas.EventosBuffetAPI.Model.Item;
import com.FnxLucas.EventosBuffetAPI.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/itens")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Item itemRequest) {
        try {
            if (itemRequest.getNomeItem() == null || itemRequest.getNomeItem().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome do item é obrigatório.");
            }
            Integer qtd = itemRequest.getQuantidade() != null ? itemRequest.getQuantidade() : 0;
            BigDecimal valUnit = itemRequest.getValorUnitario() != null ? itemRequest.getValorUnitario() : BigDecimal.ZERO;
            itemRequest.setValorTotal(valUnit.multiply(BigDecimal.valueOf(qtd)));

            Item itemSalvo = itemRepository.save(itemRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(itemSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Item>> listar() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Item itemRequest) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado");
        }
        Item item = itemOptional.get();
        if (itemRequest.getNomeItem() != null && !itemRequest.getNomeItem().trim().isEmpty()) {
            item.setNomeItem(itemRequest.getNomeItem());
        }
        item.setCategoria(itemRequest.getCategoria());
        if (itemRequest.getQuantidade() != null) {
            item.setQuantidade(itemRequest.getQuantidade());
        }
        if (itemRequest.getValorUnitario() != null) {
            item.setValorUnitario(itemRequest.getValorUnitario());
        }
        Integer qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
        BigDecimal valUnit = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
        item.setValorTotal(valUnit.multiply(BigDecimal.valueOf(qtd)));

        Item itemAtualizado = itemRepository.save(item);
        return ResponseEntity.ok(itemAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado");
        }
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
