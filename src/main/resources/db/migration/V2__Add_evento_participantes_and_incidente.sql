-- V2__Add_evento_participantes_and_incidente.sql
-- Adiciona a tabela de elementos da Collection `participantesAprovados` exigida pelo Hibernate JPA
CREATE TABLE evento_participantes (
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL,
    PRIMARY KEY (evento_id, participante_id)
);

-- Adiciona a coluna faltante mapeada no modelo Jpa de Evento (`incidenteReportado`)
ALTER TABLE role_evento 
ADD COLUMN incidente_reportado BOOLEAN NOT NULL DEFAULT FALSE;
