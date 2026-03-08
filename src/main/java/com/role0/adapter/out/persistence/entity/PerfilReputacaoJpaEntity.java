package com.role0.adapter.out.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfil_reputacao")
public class PerfilReputacaoJpaEntity {

    @Id
    @Column(name = "usuario_id", updatable = false, nullable = false)
    private UUID usuarioId;

    @Column(nullable = false)
    private double currentScore;

    @Column(nullable = false)
    private int totalAvaliacoes;

    // Default constructor for JPA
    protected PerfilReputacaoJpaEntity() {}
    
    public PerfilReputacaoJpaEntity(UUID usuarioId, double currentScore, int totalAvaliacoes) {
        this.usuarioId = usuarioId;
        this.currentScore = currentScore;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public double getCurrentScore() { return currentScore; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
}
