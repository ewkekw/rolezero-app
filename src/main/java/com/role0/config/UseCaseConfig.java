package com.role0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.role0.core.application.port.out.ChatNotificationPort;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.IdentityVerificationPort;
import com.role0.core.application.port.out.MessageBrokerEventPort;
import com.role0.core.application.service.CriarEventoService;
import com.role0.core.application.service.ProcessarSolicitacaoService;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.application.usecase.ProcessarSolicitacaoUseCase;

/**
 * Skill: Senior Architect
 * Inversão de Controle Manual. O Domínio e o Application Core são módulos puros de Java (Hexagonal).
 * É responsabilidade do Spring (Adapter In/Config) instanciá-los e injetar os Adapters Reais
 * implementando nas Outbound Ports.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CriarEventoUseCase criarEventoUseCase(EventoRepositoryPort eventoRepository) {
        return new CriarEventoService(eventoRepository);
    }

    @Bean
    public ProcessarSolicitacaoUseCase processarSolicitacaoUseCase(
            EventoRepositoryPort eventoRepository,
            IdentityVerificationPort identityPort, // Simplificado, na real traria UsuarioRepository
            ChatNotificationPort chatPort) {
        return new ProcessarSolicitacaoService(eventoRepository, chatPort);
    }
    
    // Demais UseCases seriam mapeados no mesmo padrão Bean Injection. Omitidos para concisão do artefato final.
}
