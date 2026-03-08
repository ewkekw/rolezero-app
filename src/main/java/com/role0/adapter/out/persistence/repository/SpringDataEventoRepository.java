package com.role0.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.core.domain.evento.valueobject.StatusEvento;

@Repository
public interface SpringDataEventoRepository extends JpaRepository<EventoJpaEntity, UUID> {

    /**
     * Skill: Postgres Best Practices / Database Architect
     * Utiliza-se ST_DWithin projetado em modo geography porque os dados em EPSG:4326 exigem este casting nativo
     * para efetuar cálculos perfeitos sobre o globo refrator em metros e usar Índices Espaciais (GIST) nativos sem varredura em massa (Seq Scan).
     */
    @Query(value = """
        SELECT e.* FROM eventos e 
        WHERE e.status = :status 
        AND ST_DWithin(e.localizacao\\:\\:geography, :pontoUsuario\\:\\:geography, :raioEmMetros)
        """, nativeQuery = true)
    List<EventoJpaEntity> findNearbyEventos(
            @Param("pontoUsuario") Point pontoUsuario, 
            @Param("raioEmMetros") double raioEmMetros, 
            @Param("status") String status);
}
