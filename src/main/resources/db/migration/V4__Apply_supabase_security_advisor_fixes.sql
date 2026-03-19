-- V4__Apply_supabase_security_advisor_fixes.sql
-- Essa migration aplica as recomendações de segurança (Security Advisor) sugeridas pelo Supabase.
-- Nota: ALTER EXTENSION postgis SET SCHEMA não é suportado nessa versão do PostGIS.
-- Nota: spatial_ref_sys é propriedade do superusuário PostGIS, sem permissão de alterar RLS.

-- 1. Garantir schema de extensões isolado
CREATE SCHEMA IF NOT EXISTS extensions;

-- 2. FUNÇÃO COM SEARCH PATH MUTÁVEL
-- Redefinindo a função `update_modified_column` para forçar um search_path estrito
-- e evitar injeção de dependência na execução do trigger.
CREATE OR REPLACE FUNCTION public.update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql' SET search_path = '';

-- 3. RLS - Row Level Security em tabelas de propriedade da aplicação
ALTER TABLE public.role_perfil_reputacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_usuario ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_participantes_aprovados ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_evento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_solicitacao_participacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_estabelecimento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.evento_participantes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.usuario_vibe_tags ENABLE ROW LEVEL SECURITY;
