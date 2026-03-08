package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import com.role0.core.domain.evento.valueobject.StatusEvento;

public record EventoCardResponse(
    UUID id,
    String titulo,
    int vagasRestantes,
    StatusEvento status,
    LocalDateTime horarioInicio,
    double distanciaEmMetros
) {}
