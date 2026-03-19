-- V5: Token Blacklist Table
CREATE TABLE tokens_revogados (
    token_hash VARCHAR(64) PRIMARY KEY,
    data_revogacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE tokens_revogados ENABLE ROW LEVEL SECURITY;
