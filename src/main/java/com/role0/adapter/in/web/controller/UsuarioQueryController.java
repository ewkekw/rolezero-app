package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.core.application.usecase.BuscarPerfilUsuarioUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "4. Usuários e Perfil", description = "Query e Mutation dos dados dos Participantes.")
public class UsuarioQueryController {

    private final BuscarPerfilUsuarioUseCase buscarPerfilUsuarioUseCase;

    public UsuarioQueryController(BuscarPerfilUsuarioUseCase buscarPerfilUsuarioUseCase) {
        this.buscarPerfilUsuarioUseCase = buscarPerfilUsuarioUseCase;
    }

    @Operation(summary = "Meu Perfil Completo", description = "Retorna os dados sensíveis do próprio usuário autenticado, incluindo e-mail.")
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> buscarMeuPerfil() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID usuarioAutenticadoId = UUID.fromString(principal);

        UsuarioPerfilResponse response = buscarPerfilUsuarioUseCase.executar(usuarioAutenticadoId);

        return ResponseEntity.ok(response);
    }
}
