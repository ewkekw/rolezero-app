-- V7: Criar tabela de Avaliações de Usuário
-- Permite que participantes se avaliem reciprocamente após um evento.

CREATE TABLE role_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliador_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    avaliado_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    evento_id UUID REFERENCES role_evento(id) ON DELETE SET NULL,
    nota INTEGER NOT NULL CHECK (nota >= 1 AND nota <= 5),
    comentario TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Cada avaliador pode avaliar um avaliado por evento apenas uma vez
    UNIQUE (avaliador_id, avaliado_id, evento_id)
);

CREATE INDEX idx_avaliacao_avaliado ON role_avaliacao(avaliado_id, created_at DESC);

ALTER TABLE role_avaliacao ENABLE ROW LEVEL SECURITY;
