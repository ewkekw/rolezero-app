package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para criação de um novo Rolê privativo.")
public record CriarEventoRequest(
    @Schema(description = "Título chamativo do evento", example = "Resenha do Japa")
    @NotBlank(message = "O título do rolê é obrigatório.")
    String titulo,

    @Min(value = 2, message = "O evento precisa ter pelo menos 2 vagas.")
    @Max(value = 50, message = "O limite atual do Role-Zero é de 50 vagas.")
    int capacidadeMaxima,

    @Schema(description = "Latitude exata do local presencial", example = "-23.550520")
    @NotNull(message = "A latitude é obrigatória.")
    @Min(-90) @Max(90)
    Double latitude,

    @Schema(description = "Longitude exata do local presencial", example = "-46.633308")
    @NotNull(message = "A longitude é obrigatória.")
    @Min(-180) @Max(180)
    Double longitude,

    @Schema(description = "Horário de início programado", example = "2026-12-31T22:00:00")
    @NotNull(message = "O horário de início é obrigatório.")
    @Future(message = "O rolê deve ser planejado para o futuro.")
    LocalDateTime horarioInicio
) {}
