package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.role0.core.domain.evento.valueobject.SolicitacaoParticipacao;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<Void> intençaoParticipar(@PathVariable UUID id) {
        UUID guestMockId = UUID.randomUUID();
        SolicitacaoParticipacao sol = solicitarParticipacaoUseCase.executar(id, guestMockId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Host: Julgar Solicitação Pendente", description = "Aceitar ou Rejeitar um participante da queue. Aciona Service Gatilho Social na aprovação.")
    @PutMapping("/{id}/requests/{reqId}")
    public ResponseEntity<Void> julgarSolicitacao(@PathVariable UUID id, @PathVariable UUID reqId, @Valid @RequestBody JulgarSolicitacaoRequest payload) {
        UUID hostMockId = UUID.randomUUID(); // TODO: JWT filter

        if (Boolean.TRUE.equals(payload.aprovada())) {
            processarSolicitacaoUseCase.aprovar(id, hostMockId, reqId);
        } else {
            processarSolicitacaoUseCase.recusar(id, hostMockId, reqId);
        }

        return ResponseEntity.ok().build();
    }
}
