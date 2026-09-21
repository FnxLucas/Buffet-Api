package com.FnxLucas.EventosBuffetAPI.Repository;

import com.FnxLucas.EventosBuffetAPI.Model.Evento;
import com.FnxLucas.EventosBuffetAPI.Model.StatusENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByStatus(StatusENUM status);

    List<Evento> findByDataEventoBetween(Date dataInicio, Date dataFim);

    List<Evento> findByDataEventoBetweenAndStatus(Date dataInicio, Date dataFim, StatusENUM status);

    List<Evento> findByDataEventoGreaterThanEqual(Date dataInicio);

    List<Evento> findByDataEventoLessThanEqual(Date dataFim);

    long countByStatus(StatusENUM status);

    @Query("SELECT e FROM Evento e WHERE (:inicio IS NULL OR e.dataEvento >= :inicio) AND (:fim IS NULL OR e.dataEvento <= :fim) AND (:status IS NULL OR e.status = :status) ORDER BY e.dataEvento ASC, e.horarioInicio ASC")
    List<Evento> findEventosFiltrados(@Param("inicio") Date inicio, @Param("fim") Date fim, @Param("status") StatusENUM status);
}
