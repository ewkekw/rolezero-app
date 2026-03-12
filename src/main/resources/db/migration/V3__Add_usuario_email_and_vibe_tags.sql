-- V3__Add_usuario_email_and_vibe_tags.sql
-- Adiciona o email essencial para Autenticação que ficou de fora da V1
ALTER TABLE role_usuario
ADD COLUMN email VARCHAR(255) UNIQUE;

-- Configura todos os emails nulos para algo como fallback provisório para não conflitos na constraint
UPDATE role_usuario SET email = concat(id::text, '@mock.com') WHERE email IS NULL;
ALTER TABLE role_usuario ALTER COLUMN email SET NOT NULL;

-- Remove array arcaico do postgis a favor do Table Collection format standard do JPA/Hibernate
ALTER TABLE role_usuario DROP COLUMN IF EXISTS vibe_tags;

-- Tabela associativa estrita exigida pelo @ElementCollection MapStruct das listas do Hibernate
CREATE TABLE usuario_vibe_tags (
    usuario_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, tag)
);
