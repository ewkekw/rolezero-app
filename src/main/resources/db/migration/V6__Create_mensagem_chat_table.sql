CREATE TABLE role_mensagem_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    conteudo TEXT NOT NULL,
    timestamp_envio TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    tipo VARCHAR(20) NOT NULL DEFAULT 'TEXT'
);

CREATE INDEX idx_mensagem_chat_evento ON role_mensagem_chat(evento_id, timestamp_envio DESC);

ALTER TABLE role_mensagem_chat ENABLE ROW LEVEL SECURITY;
