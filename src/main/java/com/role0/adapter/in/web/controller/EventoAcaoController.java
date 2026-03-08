package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.CheckInRequest;
import com.role0.adapter.in.web.dto.request.CriarEventoRequest;
import com.role0.core.application.usecase.AcionarBotaoPanicoUseCase;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.application.usecase.RealizarCheckInUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller focado puramente nas Ações Mutacionais do Evento.
 * Não realiza queries, não gera DTOs massivos, apenas orquestra intenções à camada Application.
 */
@RestController
@RequestMapping("/api/v1/events")
@Validated
@Tag(name = "3. Interações com Eventos", description = "Comandos transacionais (Criar, CheckIn e SOS) sobre Eventos.")
public class EventoAcaoController {

    private final CriarEventoUseCase criarEventoUseCase;
    private final RealizarCheckInUseCase realizarCheckInUseCase;
    private final AcionarBotaoPanicoUseCase acionarBotaoPanicoUseCase;

    public EventoAcaoController(
            CriarEventoUseCase criarEventoUseCase,
            RealizarCheckInUseCase realizarCheckInUseCase,
            AcionarBotaoPanicoUseCase acionarBotaoPanicoUseCase) {
        this.criarEventoUseCase = criarEventoUseCase;
        this.realizarCheckInUseCase = realizarCheckInUseCase;
        this.acionarBotaoPanicoUseCase = acionarBotaoPanicoUseCase;
    }

    @Operation(summary = "Criar Evento Novo", description = "Host cria um evento restrito. Necessita Bearer Token.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado com sucesso com Location formatado"),
        @ApiResponse(responseCode = "400", description = "Validation Fail no DTO")
    })
    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody CriarEventoRequest request) {
        UUID hostIdAutenticadoMock = UUID.randomUUID(); // Será gerado via Security Context
        
        CoordenadaGeografica localizacao = new CoordenadaGeografica(request.latitude(), request.longitude());
        Evento eventoCriado = criarEventoUseCase.executar(
            hostIdAutenticadoMock, 
            request.titulo(), 
            request.capacidadeMaxima(), 
            localizacao, 
            request.horarioInicio()
        );
        
        // Retorna HTTP 201 Created apontando para URI do novo recurso (Location header logic omitida por brevidade)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Finalizar CheckIn Definitivo", description = "Disponível apenas para participantes já aprovados e que cruzem o raio de 50m físico via Geometria Euclidiana do Domínio.")
    @PostMapping("/{id}/check-in")
    public ResponseEntity<Void> efetuarCheckIn(@PathVariable UUID id, @Valid @RequestBody CheckInRequest request) {
        UUID userContextMock = UUID.randomUUID(); 
        realizarCheckInUseCase.executar(id, userContextMock, new CoordenadaGeografica(request.latitude(), request.longitude()));
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint crítico para segurança física de usuários.
     * Acionado durante um evento para sinalizar ao backend retenção forense de logs da party (conforme ADR-001).
     */
    @Operation(summary = "🔥 Botão de Pânico (SOS)", description = "Acorta Ciclos da Fila. Ativa Congelamento Forense da Sala (WORM AWS S3). Tratamento cego e tolerante a falhas (202 Accepted sempre) para não vazar pânico a terceiros.")
    @PostMapping("/{id}/panic")
    public ResponseEntity<Void> acionarEmergencia(@PathVariable UUID id) {
        UUID reportadorContextMock = UUID.randomUUID();
        acionarBotaoPanicoUseCase.executar(id, reportadorContextMock);
        
        // A API responde de forma otimista/minimalista para não gerar latência ou erros obvios diante do agressor
        return ResponseEntity.accepted().build();
    }
}
