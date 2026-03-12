package com.role0.adapter.out.persistence.entity;

import java.util.UUID;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_perfil_reputacao")
public class PerfilReputacaoJpaEntity {

    @Id
    @Column(name = "usuario_id", updatable = false, nullable = false)
    private UUID usuarioId;

    @Column(name = "trust_score", nullable = false)
    private BigDecimal currentScore;

    @Column(name = "avaliacoes_totais", nullable = false)
    private int totalAvaliacoes;

    // Default constructor for JPA
    protected PerfilReputacaoJpaEntity() {
    }

    public PerfilReputacaoJpaEntity(UUID usuarioId, BigDecimal currentScore, int totalAvaliacoes) {
        this.usuarioId = usuarioId;
        this.currentScore = currentScore;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public BigDecimal getCurrentScore() {
        return currentScore;
    }

    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }
}
