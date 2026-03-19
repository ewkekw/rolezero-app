package com.role0.core.application.usecase;

import java.util.UUID;

public interface AtualizarPerfilUseCase {
    void executar(UUID usuarioId, String novoNomeDisplay);
}
