package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.JulgarSolicitacaoRequest;
import com.role0.core.application.usecase.ProcessarSolicitacaoUseCase;
import com.role0.core.application.usecase.SolicitarParticipacaoUseCase;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/events")
@Validated
@Tag(name = "4. Solicitações de Participação", description = "Workflows de aprovação e recusa entre Guest e Host.")
public class SolicitacaoController {

    private final SolicitarParticipacaoUseCase solicitarParticipacaoUseCase;
    private final ProcessarSolicitacaoUseCase processarSolicitacaoUseCase;

    public SolicitacaoController(
            SolicitarParticipacaoUseCase solicitarParticipacaoUseCase,
            ProcessarSolicitacaoUseCase processarSolicitacaoUseCase) {
        this.solicitarParticipacaoUseCase = solicitarParticipacaoUseCase;
        this.processarSolicitacaoUseCase = processarSolicitacaoUseCase;
    }

    @Operation(summary = "Guest: Pedir para Participar", description = "Adiciona o usuário numa Queue pendente a ser julgada pelo dono da festa (Host).")
    @PostMapping("/{id}/join-requests")
    public ResponseEntity<Void> intençaoParticipar(
            @Parameter(description = "UUID do Evento Alvo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        UUID guestMockId = UUID.randomUUID();
        solicitarParticipacaoUseCase.executar(id, guestMockId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Host: Julgar Solicitação Pendente", description = "Aceitar ou Rejeitar um participante da queue. Aciona Service Gatilho Social na aprovação.")
    @PutMapping("/{id}/requests/{reqId}")
    public ResponseEntity<Void> julgarSolicitacao(
            @Parameter(description = "UUID do Evento sendo moderado", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, 
            @Parameter(description = "UUID da Request efetuada pelo participante", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID reqId,
            @Valid @RequestBody JulgarSolicitacaoRequest payload) {
        // Extrai o ID do Host do Token JWT validado no SecurityContext
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID hostIdAutenticado = UUID.fromString(principal);

        if (Boolean.TRUE.equals(payload.aprovada())) {
            processarSolicitacaoUseCase.aprovar(id, hostIdAutenticado, reqId);
        } else {
            processarSolicitacaoUseCase.recusar(id, hostIdAutenticado, reqId);
        }

        return ResponseEntity.ok().build();
    }
}
