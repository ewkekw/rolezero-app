package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarPerfilRequest(
    @NotBlank(message = "O nome de exibição não pode ser vazio.")
    String nomeDisplay
) {}
