package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public void validarEventoId(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new IllegalArgumentException("Erro: Evento não encontrado");
        }
    }
}
