# ADR 002: Adoção do PostgreSQL com Extensão PostGIS

**Status:** Aceito
**Data:** 08-03-2026

## Contexto
O ecossistema fundamental do aplicativo "Role0" gira em torno de encontrar e participar de eventos relâmpagos (rolês efêmeros) que estejam *fisicamente* próximos ao usuário (conceito de hiperlocalidade). 
O fluxo principal da Home requer a listagem de eventos dentro de varreduras radiais em tempo real (ex: "Me dê eventos acontecendo agora em um raio de 5km de onde estou"), filtrados pelas preferências em comum com o anfitrião e pela capacidade (não lotados). Além disso, o check-in exige prova de proximidade real (estar a menos de 50 metros do hospedeiro).

## Alternativas Consideradas
1. **MongoDB (GeoJSON e 2dsphere indexes):** Boa performance e flexibilidade no NoSQL. No entanto, o projeto possui relacionamentos transacionais complexos (Aprovações de Vaga dependem de Capacidade e Status).
2. **ElasticSearch:** Velocidade absoluta em queries espaciais. Mas exige complexidade extra para sincronia de dados e uma segunda infraestrutura apartada do banco de dados relacional (Single Source of Truth), encarecendo custos.
3. **Cálculo de Distância Euclidiana em Runtime (Java):** Recuperar todos os Eventos num raio maior, mapeá-los em memória no servidor da aplicação usando a fórmula de Haversine (`Math.sin() / Math.cos()`) e retorná-los. Ruim para o Garbage Collector e fatal se a tabela de Eventos ativos crescer.

## Decisão
Foi escolhido o uso orgânico do **PostgreSQL** incrementado nativamente com a biblioteca **PostGIS**.
Isso nos garante Transacionabilidade (ACID) das manipulações do Match/Aprovação simultaneamente sendo o banco principal onde guardamos dados de usuários, e uma performance estupenda rodando tipos `GEOGRAPHY(Point,4326)` através da query `ST_DWithin` combinada a um Index espacial Genérico (`GIST`).
Foi optada a integração da biblioteca "Hibernate Spatial" ao Spring Data para evitar conversões exaustivas de SQL nativo dentro do mapeamento das Entidades, além do espelhamento do banco real hospedado em nuvem (**Supabase M-Tier**).

## Consequências
### Positivas
- Redução agressiva de tempo de resposta nas queries do Mapa (de Segundos para Milissegundos).
- Mantém a arquitetura limpa em stack única transacional (1 banco faz o relacional e a indexação de mapa).

### Negativas
- Dependência externa impõe uso exclusivo da imagem `postgis/postgis` via Docker.
- Complicação sintaxe do `V1__Initial_schema.sql` (Exige ativação da `extension` na inicialização do DDL).
