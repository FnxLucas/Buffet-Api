package com.FnxLucas.EventosBuffetAPI.Repository;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByNome(String nome);
}
