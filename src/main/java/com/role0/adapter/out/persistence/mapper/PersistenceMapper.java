package com.role0.adapter.out.persistence.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;

@Mapper
public abstract class PersistenceMapper {

    public static final PersistenceMapper INSTANCE = Mappers.getMapper(PersistenceMapper.class);

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // -- Evento --

    @Mapping(target = "localizacao", source = "localizacao", qualifiedByName = "coordenadaToPoint")
    public abstract EventoJpaEntity toJpaEntity(Evento dominio);

    @Mapping(target = "localizacao", source = "localizacao", qualifiedByName = "pointToCoordenada")
    public abstract Evento toDomain(EventoJpaEntity entity);

    // -- Usuario --

    @Mapping(target = "provedIdentityToken", source = "biometriaValidada")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "tags", source = "vibeTags")
    public abstract UsuarioJpaEntity toJpaEntity(Usuario dominio);

    @Mapping(target = "biometriaValidada", source = "provedIdentityToken")
    @Mapping(target = "vibeTags", source = "tags")
    @Mapping(target = "tokenVerificacaoBiometrica", ignore = true)
    public abstract Usuario toDomain(UsuarioJpaEntity entity);

    // -- Spatial Converters (WGS84) --

    @Named("coordenadaToPoint")
    protected Point coordenadaToPoint(CoordenadaGeografica loc) {
        if (loc == null)
            return null;
        // JTS receives Longitude (X), Latitude (Y)
        return geometryFactory.createPoint(new Coordinate(loc.getLongitude(), loc.getLatitude()));
    }

    @Named("pointToCoordenada")
    protected CoordenadaGeografica pointToCoordenada(Point point) {
        if (point == null)
            return null;
        // Extracts Latitude (Y), Longitude (X)
        return new CoordenadaGeografica(point.getY(), point.getX());
    }
}
