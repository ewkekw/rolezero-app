# Diagramas de Arquitetura: Role0

Abaixo estão as representações visuais em *MermaidJS* da arquitetura do aplicativo. Você pode usar uma extensão de pré-visualização no VSCode ou lançar no Live Editor web.

## 1. C4 Model (Container View)
Visão sistêmica descrevendo os componentes interativos do Macro-Ecossistema Role0 e as fronteiras do Hexágono de backend:

```mermaid
C4Container
    title Container diagram for Role0 Ecosystem

    Person(user, "Usuário Mob", "Um indivíduo querendo sair para um Rolê.")
    Person(establishment, "B2B Bar/Rest", "Gestor do estabelecimento.")

    System_Boundary(role0_sys, "Sistema Core Role0") {
        Container(mobile_app, "Mobile App", "Flutter / Dart", "Garante o GPS, Biometria Camera e UI.")
        Container(web_app, "Web Portal B2B", "React", "Portal para bares.")
        
        Container(backend_api, "Backend Core API", "Java 21, Spring Boot", "Motor Hexagonal que roteia o DDD de Conexão. Stateless.")
        
        ContainerDb(db, "Database", "PostgreSQL + PostGIS", "Armazena usuários, reputação, logs de auditoria e os dados espaciais dos rolês.")
        ContainerDb(cache, "Mapeamento em Memória", "Redis", "Filtragem geo-radial veloz para 'Eventos Próximos' (Raio de 5~10km).")
        ContainerDb(message_broker, "Delayed Messaging", "RabbitMQ", "Fila assíncrona para expirar e matar eventos 24h após iniciarem.")
    }

    System_Ext(sso_provider, "SSO / OAuth 2.0", "Google / Apple Sign-in.")
    System_Ext(biometry_api, "AWS Liveness", "Checa vitalidade anti-fake em zero-knowledge.")

    Rel(user, mobile_app, "Usa o mapa de calor e chat via socket", "HTTPS/WSS")
    Rel(establishment, web_app, "Dá lances das mesas via site oficial", "HTTPS")

    Rel(mobile_app, backend_api, "Faz requisições REST/STOMP. Bearer JWT.", "HTTPS/JSON")
    Rel(web_app, backend_api, "Autentica e edita mesas parceiras.", "HTTPS/JSON")

    Rel(backend_api, db, "Lê e Salva as Agregações JPA, Faz query ST_DWithin", "JDBC")
    Rel(backend_api, cache, "Mapeia radar instantâneo e rate limits", "TCP")
    Rel(backend_api, message_broker, "Agenda encerramento programado", "AMQP")

    Rel(backend_api, sso_provider, "Troca token base no backend e consolida login", "HTTPS")
    Rel(backend_api, biometry_api, "Verifica imagem crua remotamente para dar 'Ativo'", "HTTPS")
```

---

## 2. Matchmaking Workflow (Fluxograma da Conexão)
Como um evento nasce, transita até lotar, e se extingue fisicamente:

```mermaid
sequenceDiagram
    autonumber
    
    participant Host as Host (Anfitrião)
    participant Guest as Guest (Convidado)
    participant API as API Core (Java)
    participant Dom as Domínio Core
    participant PostGIS as DB (PostGIS)
    participant Broker as RabbitMQ
    
    Note over Host, PostGIS: Fase: Descoberta e Abertura
    
    Host->>API: [POST] /events (Local: Pub X, 4 Vagas)
    API->>Dom: criar evento()
    Dom-->>API: (Valida Regras e Invariantes)
    API->>PostGIS: Save Evento com Point Geom
    
    Guest->>API: [GET] /events/nearby (Estou num raio de 5km)
    API->>PostGIS: Executa ST_DWithin(5000)
    PostGIS-->>API: Devolve vetor de Eventos no Raio
    API-->>Guest: Mostra Card do Pub X
    
    Note over Host, PostGIS: Fase: Conexão e Match
    
    Guest->>API: [POST] /events/{id}/join-requests (Pedir vaga)
    API->>Host: (Websocket) "Alguém quer entrar"
    Host->>API: [PUT] .../requests/{id} (Ação: APPROVE)
    API->>Dom: aprovarParticipante(Guest_ID)
    API->>PostGIS: Add 'Aprovados' Associative Table
    
    opt Mesa Fica Lotada (Pre-Game Start)
        API->>Broker: Despacha "Matar Evento D+24h" Timestamp
        API->>Dom: GatilhoSocialService (Analisa Tags dos 4 Integrantes)
        Dom-->>API: Interseção encontrada: "Jogos Indie"
        API->>Host: (Websocket) "Vagas Esgotadas! Seu Icebreaker é..."
        API->>Guest: (Websocket) "Você entrou na sala. Fala galera!"
    end
    
    Note over Host, Broker: Fase Efêmera e Exclusão
    
    Note right of Broker: 24 horas passam na vida real.
    Broker-->>API: Consumidor Acorda: Encerrar(UUID_PUB)
    API->>PostGIS: Status = EXPIRADO, Deleta Mensagens de Chat se não ouver Flag de Pânico
```
