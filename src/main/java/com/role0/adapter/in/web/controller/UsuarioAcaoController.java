package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.AtualizarPerfilRequest;
import com.role0.core.application.usecase.AtualizarPerfilUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "4. Usuários e Perfil")
public class UsuarioAcaoController {

    private final AtualizarPerfilUseCase atualizarPerfilUseCase;

    public UsuarioAcaoController(AtualizarPerfilUseCase atualizarPerfilUseCase) {
        this.atualizarPerfilUseCase = atualizarPerfilUseCase;
    }

    @Operation(summary = "Atualizar Meu Perfil", description = "Permite que o usuário autenticado atualize informações mutáveis básicas do seu próprio perfil.")
    @PatchMapping("/me")
    public ResponseEntity<Void> atualizarMeuPerfil(@Valid @RequestBody AtualizarPerfilRequest request) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID usuarioAutenticadoId = UUID.fromString(principal);

        atualizarPerfilUseCase.executar(usuarioAutenticadoId, request.nomeDisplay());

        return ResponseEntity.noContent().build();
    }
}
