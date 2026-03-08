# ADR 001: Adoção de Arquitetura Hexagonal (Ports and Adapters) no Backend Core

**Status:** Aceito
**Data:** 08-03-2026

## Contexto
O Role0 depende intensamente de regras de negócio estritas a respeito da criação, limites e ciclos de vida de Eventos (Rolês). Ao mesmo tempo, ele precisa interagir com múltiplas realidades externas: Notificações Push via Socket, Persistência Geoespacial (PostGIS), integrações Biométricas e Mensageria (EDA para encerramento de evento).
Desenvolver essa lógica usando uma arquitetura tradicional em Camadas (`Controller -> Service -> Repository`) acoplaria inevitavelmente nossa lógica de negócio aos frameworks (`@Transactional`, `@Data`, `@Entity`), tornando refatorações custosas e limitando a testabilidade.

## Decisão
Adotamos o padrão **Ports and Adapters** (Arquitetura Hexagonal) idealizado por Alistair Cockburn combinada a princípios de Domain-Driven Design (DDD).
A aplicação foi fisicamente dividida em:
1.  **Domain:** Entidades puras Java (Sem frameworks). Invariantes rigorosas.
2.  **Application:** Portas de Entrada (`UseCases`) e Portas de Saída (`Ports/SPIs`). Pura lógica de orquestração.
3.  **Adapters:** Implementações externas plásticas que orbitam o centro. In e Out.
4.  **Config:** Injeção de dependência e Wiring manual do Spring.

## Consequências
### Positivas
- **Alta Testabilidade:** É possível testar o coração do sistema instantaneamente via Junit TDD (mocks nas portas) sem carregar o contexto pesado de Spring Boot.
- **Isolamento Tecnológico:** Se o mecanismo do PostGIS mudar, ou substituirmos RabbitMQ por Kafka, o código da camada de Aplicação sequer ficará sabendo da existência dessa alteração.

### Negativas
- **Curva de Aprendizado / Verbosidade:** O número de pacotes cresce consideravelmente (necessidade de modelar Entidades do Domínio e Entidades JPA separadas, e o respectivo `Mapper` entre elas). A configuração de Beans manuais pode assustar devs iniciantes no ecosistema Spring.
