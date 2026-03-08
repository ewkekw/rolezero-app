package com.role0.adapter.in.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.SsoLoginRequest;
import com.role0.adapter.in.web.dto.response.TokenResponse;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "1. Autenticação (SSO & Biometria)", description = "Endpoints públicos de onboarding e login. Nenhuma autenticação exigida aqui.")
public class AuthController {

    // Dependency injection to be configured via UseCaseConfig in Phase 6
    // private final LoginSsoUseCase loginSsoUseCase;

    @Operation(summary = "SSO Login", description = "Fornece um Identity Token Externo gerando um Bearer JWT Stateless assinado pela Role-Zero.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão criada e Access Token emitido"),
        @ApiResponse(responseCode = "400", description = "SSO Token Inválido ou Malformado")
    })
    @PostMapping("/sso")
    public ResponseEntity<TokenResponse> ssoLogin(@Valid @RequestBody SsoLoginRequest request) {
        // Validação SSO Mockada para este adaptador no momento
        TokenResponse response = new TokenResponse("jwt_token_stub", 3600);
        return ResponseEntity.ok(response);
    }
}
