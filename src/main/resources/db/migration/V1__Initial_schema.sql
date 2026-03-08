-- V1__Initial_schema.sql
-- Modelagem de Banco de Dados Extremamente Bem Feita para o Role0
-- Focada em performance, integridade e uso do PostGIS.

-- 1. Habilitar PostGIS para consultas geoespaciais
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Tabela de Usuários (Agregação de Identidade e Autenticidade)
CREATE TABLE role_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    biometria_validada BOOLEAN NOT NULL DEFAULT FALSE,
    token_verificacao_biometrica VARCHAR(255),
    vibe_tags VARCHAR(50)[] DEFAULT '{}', -- Array nativo do Postgres, limite validado no backend
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index para buscas fáceis de usuários ativos
CREATE INDEX idx_usuario_biometria ON role_usuario(biometria_validada);

-- 3. Tabela de Reputação (Agregação de Reputação/Gamificação separada do fluxo principal)
CREATE TABLE role_perfil_reputacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES role_usuario(id) ON DELETE CASCADE,
    trust_score DECIMAL(3, 2) NOT NULL DEFAULT 0.00 CHECK (trust_score >= 0.00 AND trust_score <= 5.00),
    avaliacoes_totais INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabela de Estabelecimentos (Agregação Módulo B2B)
CREATE TABLE role_estabelecimento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    localizacao GEOMETRY(Point, 4326) NOT NULL,
    parceiro_oficial BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index espacial para buscas rápidas de estabelecimentos no mapa
CREATE INDEX idx_estabelecimento_localizacao ON role_estabelecimento USING GIST (localizacao);

-- 5. Tabela Principal: Evento (A Alma do Role0)
CREATE TABLE role_evento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    host_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE RESTRICT,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    capacidade_maxima INTEGER NOT NULL CHECK (capacidade_maxima > 0),
    status VARCHAR(30) NOT NULL DEFAULT 'CRIADO',
    localizacao GEOMETRY(Point, 4326) NOT NULL,
    horario_inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index espacial crítico para a busca "O Mapa" (Radar)
CREATE INDEX idx_evento_localizacao_gist ON role_evento USING GIST (localizacao);

-- Index B-Tree composto para filtrar eventos abertos geograficamente e temporalmente
CREATE INDEX idx_evento_status_inicio ON role_evento(status, horario_inicio);

-- 6. Tabela de Relacionamento N:N (Participantes Aprovados no Evento)
CREATE TABLE role_participantes_aprovados (
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    aprovado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (evento_id, participante_id)
);

-- 7. Tabela de Solicitações de Vaga (Intenção de Participação - Contexto de Conexões)
CREATE TABLE role_solicitacao_participacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    status_solicitacao VARCHAR(30) NOT NULL DEFAULT 'PENDENTE', -- PENDENTE, APROVADA, REJEITADA
    data_solicitacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- Uma pessoa só pode solicitar vaga 1 vez por evento
    UNIQUE (evento_id, usuario_id) 
);

-- Index para o Anfitrião carregar rápido as solicitações do seu evento
CREATE INDEX idx_solicitacao_evento ON role_solicitacao_participacao(evento_id, status_solicitacao);

-- 8. Triggers Funcionais para Atualização de Timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trg_update_usuario_updated_at BEFORE UPDATE ON role_usuario FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_reputacao_updated_at BEFORE UPDATE ON role_perfil_reputacao FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_estabelecimento_updated_at BEFORE UPDATE ON role_estabelecimento FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_evento_updated_at BEFORE UPDATE ON role_evento FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
