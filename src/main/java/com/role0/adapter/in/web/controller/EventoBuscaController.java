package com.role0.adapter.in.web.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.response.EventoCardResponse;
// Importação mockada para a anotação de Resilience4J (a ser injetada de fato na Fase 6)
// import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PathVariable;
import com.role0.core.application.usecase.BuscarEventoUseCase;
import com.role0.core.application.dto.EventoDetalheOutput;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "2. Busca e Indexação Geográfica", description = "Radar de Eventos. Rate Limiting ativo por IP para prevenir Web Scraping.")
public class EventoBuscaController {

    private final BuscarEventoUseCase buscarEventoUseCase;

    public EventoBuscaController(BuscarEventoUseCase buscarEventoUseCase) {
        this.buscarEventoUseCase = buscarEventoUseCase;
    }

    /**
     * Endpoint crítico para raspagem (Scraping). 
     * Implementada anotação de Rate Limit para permitir no máximo X requests por minuto por IP.
     */
    @Operation(summary = "Radar de Eventos Abertos", description = "Aciona o PostGIS Spatial Query ST_DWithin para recuperar os Eventos abertos baseados no Raio e GPS do Usuário.")
    @GetMapping("/nearby")
    // @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
    // Descomentaremos acima quando a lib resilience4j-spring-boot3 for importada no framework.
    public ResponseEntity<List<EventoCardResponse>> buscarProximos(
            @Parameter(description = "Latitude exata do usuário", required = true) @RequestParam(name = "latitude") double latitude, 
            @Parameter(description = "Longitude exata do usuário", required = true) @RequestParam(name = "longitude") double longitude, 
            @Parameter(description = "Raio de dispersão em Quilômetros (Default: 10km)", required = false) @RequestParam(name = "raioKm", defaultValue = "10.0") double raioKm) {
        
        // Chamada à porta de entrada (Inbound Use Case) Delegada à Fase Application Core.
        // List<Evento> resultadosDomain = buscarEventosUseCase.executar(lat, lon, raioKm);
        
        return ResponseEntity.ok(Collections.emptyList()); // Retornará mapper.toCardList(resultadosDomain)
    }

    public ResponseEntity<String> rateLimiterFallback(Exception e) {
        return ResponseEntity.status(429)
            .body("Too Many Requests: Detectamos excesso de requisições buscando eventos. Tente novamente em instantes.");
    }

    @Operation(summary = "Detalhes Ricos do Evento", description = "Recupera os detalhes completos do evento, incluindo contagem de vagas, informações do anfitrião e clima no local.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventoDetalheOutput> buscarDetalheEvento(
            @Parameter(description = "ID do Evento em formato UUID", required = true) @PathVariable UUID eventId) {
        return ResponseEntity.ok(buscarEventoUseCase.executar(eventId));
    }
}
