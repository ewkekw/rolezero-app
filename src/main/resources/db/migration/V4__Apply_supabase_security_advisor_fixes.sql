-- V4__Apply_supabase_security_advisor_fixes.sql
-- Essa migration aplica as recomendações de segurança (Security Advisor) sugeridas pelo Supabase.

-- 1. EXTENSÃO POSTGIS - Mover do schema 'public' para o schema 'extensions'
-- Supabase alerta quando extensões ficam expostas no schema public.
CREATE SCHEMA IF NOT EXISTS extensions;
-- A alteração de schema requer update no pg_extension em algumas versões, mas no geral basta o ALTER.
ALTER EXTENSION postgis SET SCHEMA extensions;

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

-- 3. RLS - Row Level Security desabilitado no schema public
-- Como a aplicação usa a conexão 'postgres' (backend-driven), o RLS será "bypassed" pelo Spring Boot,
-- mas ativa a barreira de segurança exigida pelo Data API nativo do Supabase.
ALTER TABLE public.spatial_ref_sys ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_perfil_reputacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_usuario ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_participantes_aprovados ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_evento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_solicitacao_participacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_estabelecimento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.evento_participantes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.flyway_schema_history ENABLE ROW LEVEL SECURITY;

-- Nota: Ativamos também na tabela flyway_schema_history conforme as exigências restritas do Supabase.
