package com.role0.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.adapter.out.persistence.mapper.PersistenceMapper;
import com.role0.adapter.out.persistence.repository.SpringDataEventoRepository;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.domain.evento.entity.Evento;

@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {

    private final SpringDataEventoRepository repository;
    private final PersistenceMapper mapper;
    private final GeometryFactory geometryFactory;

    public EventoRepositoryAdapter(SpringDataEventoRepository repository, PersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
    }

    @Override
    @CacheEvict(value = "eventosCache", allEntries = true)
    public void salvar(Evento evento) {
        EventoJpaEntity entity = mapper.toJpaEntity(evento);
        repository.save(entity);
    }

    @Override
    public Optional<Evento> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Cacheable(value = "eventosCache", key = "#lat + '-' + #lon + '-' + #raioMetros")
    public List<Evento> buscarEventosProximosAbertos(double lat, double lon, double raioMetros) {
        Point pontoBuscador = geometryFactory.createPoint(new Coordinate(lon, lat));
        
        List<EventoJpaEntity> eventosProximos = repository.findNearbyEventos(
            pontoBuscador, 
            raioMetros, 
            "ABERTO_PARA_VAGAS" // Omitindo a enumeração apenas pro mock JDBC literal
        );

        return eventosProximos.stream()
            .map(mapper::toDomain)
            // Aqui, o Adapter também faria o cálculo da distância entre o Evento (A) o Usuário (B) 
            // e injetaria no transient distance para retornar ao View Layer prontas
            .collect(Collectors.toList());
    }
}
