package com.role0.core.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;

public class CriarEventoService implements CriarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    public CriarEventoService(EventoRepositoryPort eventoRepository, UsuarioRepositoryPort usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Evento executar(UUID hostId, String titulo, int capacidadeMaxima, CoordenadaGeografica localizacao, LocalDateTime horarioInicio) {
        // Validação via Outbound port (O Host precisa estar com a biometria Validada Zero-Knowledge antes de criar rolês)
        Usuario host = usuarioRepository.buscarPorId(hostId)
            .orElseThrow(() -> new EventoDomainException("Host não encontrado"));
            
        if (!host.isBiometriaValidada()) {
            throw new EventoDomainException("A validação biométrica com liveness é obrigatória para criar um rolê.");
        }

        Evento evento = new Evento(UUID.randomUUID(), host.getId(), titulo, capacidadeMaxima, localizacao, horarioInicio);
        
        // Host é automaticamente o primeiro aprovado no evento dele
        evento.aprovarParticipante(host.getId());
        
        return eventoRepository.salvar(evento);
    }
}
