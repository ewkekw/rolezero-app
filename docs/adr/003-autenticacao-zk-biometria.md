# ADR 003: Validação Biométrica Zero-Knowledge para Prevenção de Catfishing

**Status:** Aceito
**Data:** 08-03-2026

## Contexto
O cerne do **Role0** é incentivar o encontro *físico* com desconhecidos (em locais predeterminados). A principal barreira de adoção orgânica para esse estilo de Matchmaking é o **medo sistêmico de falsificação de identidade (Catfishing)** ou assédio por perfis fictícios.
Os testes com grupos focais e a ideação indicaram que o *TrustScore* (Sistema de gamificação) não seria suficiente inicialmente: antes de jogar no grupo, o perfil precisava ser verificado por vivacidade humana (Proof-of-Life).
Armazenar dados biométricos (rostos) em nossos servidores representava um fardo monstruoso sob a LGPD (Lei Geral de Proteção de Dados - Brasil) e implicava grandes gastos de custo de hospedagem estática e compliance.

## Decisão
Abolir o armazenamento interno de dados biométricos.
Adotamos um modelo **Zero-Knowledge**, integrando o fluxo de Onboarding (`/api/v1/auth/onboarding/biometry`) com um parceiro externo especializado (Ex: AWS Rekognition Liveness, ou id.wall). O aplicativo móvel encaminhará o registro/stream (Multipart Payload) até a Porta de Serviço delegada, que transmitirá exclusivamente aos servidores da Plataforma Provedora. 

Para a Agregação `Usuario` no banco da nossa plataforma, apenas será armazenado de forma orgânica e permanente a Flag Booleana (`biometriaValidada = TRUE`) e a Assinatura Hash Fornecida pelo Provedor (`tokenVerificacaoBiometrica`).

## Consequências
### Positivas
- O Role0 **nunca** portará localmente os rostos da sua base de usuários. Proteção absoluta contra um Breach Crítico (Vazamento).
- O backend não gasta dinheiro / armazenamento processando Machine Learning Models em Java para análise comparativa ou detecção de profundidade/3D Face mapping. Simplicidade.

### Negativas
- O SLA (Latência) do processo de Sign-up depende inteiramente da API de terceiros. A resiliência deste fluxo em caso de queda pontual será delegada a um Retry Policy no Client App.
