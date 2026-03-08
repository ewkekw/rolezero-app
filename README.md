# Role0 Backend Core 🍸🎭

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-PostGIS-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet.svg)

> **"A Vida Real Acontece Offline."**

Role0 é uma plataforma B2C focada em hiperlocalidade e conexões espontâneas autênticas. O aplicativo permite que usuários criem ou peçam vagas em "Rolês" efêmeros (eventos que expiram em 24h) através de um mapa em tempo real guiado pelo algoritmo de radar *PostGIS*.

Este repositório contém a **API Core (Backend)** do ecossistema, modelada 100% sobre **DDD (Domain-Driven Design)** e **Arquitetura Hexagonal (Ports & Adapters)** para garantir extrema manutenibilidade e resiliência das regras de negócio.

---

## 🎯 Por que a Arquitetura Hexagonal?
1. **Domínio Puro (Agnóstico):** O coração da aplicação (entidades como `Evento`, `Usuario`, `Reputacao`) não sabe o que é Spring, JPA ou PostgreSQL.
2. **Escalabilidade Tecnológica:** Se amanhã precisarmos trocar o banco PostgreSQL pelo MongoDB, mexemos apenas no _Driven Adapter_. O código de negócios permanece intocado.
3. **Casos de Uso Explícitos:** A pasta `usecase/` funciona como um "Menu" do que o sistema faz. Nomes descritivos como `AcionarBotaoPanicoUseCase` ou `RealizarCheckInUseCase`.

---

## 🏗️ Estrutura do Projeto

O projeto é dividido fisicamente nos seguintes anéis (Hexágonos):

- **`/core/domain`**: Entidades ricas, Value Objects e Exceções de negócio.
- **`/core/application`**: Onde a mágica acontece. Portas (Interfaces) de entrada (`UseCases`) e saída (`Ports`), além dos serviços que os implementam.
- **`/adapter/in`**: O que "chama" e interage com nosso sistema. (Web Controllers REST).
- **`/adapter/out`**: Os sistemas com quem nos comunicamos para cumprir o trabalho (JPA, WebSockets, Zero-Knowledge Biometry, Supabase).
- **`/config`**: Onde o Spring "Amarra" (Wiring) tudo isso. A inversão de controle explícita através de Config Beans e Segurança (SSO).

---

## ⚙️ Pré-requisitos
- JDK 21+ instalado.
- Maven 3.8+
- Docker & Docker Compose (Para o PostGIS/Redis Local)
- Ou uma [Supabase](https://supabase.com) URL.

## 🚀 Como Rodar Localmente (Setup de Desenvolvimento)

**1. Suba a Infraestrutura pelo Docker**
```bash
docker-compose up -d
```
> Isso fará o spin-up de um container PostgreSQL equipado com a extensão espacial **PostGIS** requerida pelo sistema.

**2. Configure as Variáveis de Ambiente**
Você precisará registrar (ou colocar num `.env` no IntelliJ):
- `DB_USER`: Usuário do banco (default: `role0_admin`)
- `DB_PASS`: Senha local.
- `JWT_SECRET`: Chave secreta longa para assinar os tokens Stateless da API.

**3. Execute via Maven**
```bash
mvn spring-boot:run
```
O *Flyway* detectará automaticamente que as tabelas não existem e executará nosso arquivo DDL ultratecnológico contido em `V1__Initial_schema.sql`, criando as estruturas e os índices geoespaciais automaticamente.

## 📚 Documentação (Wiki e ADRs)

A documentação densa e os porquês estão localizados na pasta `docs/`.
Lá você encontrará:
*   [**Architecture Decision Records (ADRs)**](./docs/adr) - O motivo de termos escolhido essa infra e frameworks.
*   **OpenAPI/Swagger** - Contrato completo para consumo da API.
*   **Fases de Planejamento Original** - Dissecação dos diagramas e fluxos.
