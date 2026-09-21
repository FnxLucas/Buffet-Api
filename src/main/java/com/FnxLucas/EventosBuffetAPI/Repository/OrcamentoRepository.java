package com.FnxLucas.EventosBuffetAPI.Repository;

import com.FnxLucas.EventosBuffetAPI.Model.Orcamento;
import com.FnxLucas.EventosBuffetAPI.Model.StatusOrcamentoENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByEventoId(Long eventoId);

    List<Orcamento> findByStatus(StatusOrcamentoENUM status);

    long countByStatus(StatusOrcamentoENUM status);

    @Query("SELECT o FROM Orcamento o WHERE (:inicio IS NULL OR (o.evento IS NOT NULL AND o.evento.dataEvento >= :inicio) OR (o.evento IS NULL AND o.validade >= :inicioLocal)) AND (:fim IS NULL OR (o.evento IS NOT NULL AND o.evento.dataEvento <= :fim) OR (o.evento IS NULL AND o.validade <= :fimLocal))")
    List<Orcamento> findOrcamentosFiltrados(@Param("inicio") Date inicio, @Param("fim") Date fim,
                                           @Param("inicioLocal") LocalDate inicioLocal, @Param("fimLocal") LocalDate fimLocal);
}
