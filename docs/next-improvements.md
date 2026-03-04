# Próximas Melhorias — Drink Water API

Itens práticos identificados após análise do projeto, ordenados por prioridade.
Nenhum destes introduz complexidade arquitetural desnecessária.

---

## Visão geral

| Prioridade | Item                                | Esforço | Status   |
| ---------- | ----------------------------------- | ------- | -------- |
| Alta       | Rate limiting                       | Médio   | Pendente |
| Alta       | Testes de validação nos controllers | Baixo   | Pendente |
| Média      | Auditoria (created_at / updated_at) | Baixo   | Pendente |
| Média      | Correlation ID nos logs e respostas | Baixo   | Pendente |
| Média      | README atualizado                   | Trivial | Pendente |
| Média      | Fonte única de test data            | Trivial | Pendente |
| Baixa      | ETag para GETs                      | Trivial | Pendente |
| Baixa      | Idempotency key para POSTs          | Médio   | Pendente |
| Baixa      | Flyway health check                 | Trivial | Pendente |

---

## Detalhamento

### 1. Rate Limiting — Prioridade Alta · Esforço Médio [DONE]

**Problema:** A API não possui proteção contra abuso de requisições. Um token OAuth2 válido pode
gerar volume ilimitado de chamadas a todos os 9 endpoints públicos.

#### Análise de alternativas

Três opções viáveis para o stack atual (Java 25, Spring Boot 3.5.11):

##### Opção A — Resilience4j `@RateLimiter` (RECOMENDADA)

O Resilience4j 2.3.0 **já está no projeto** (`resilience4j-spring-boot3`) com
`spring-boot-starter-aop` e Actuator configurados. O módulo `RateLimiter` está incluído
no BOM sem dependência adicional.

| Aspecto                     | Detalhe                                                               |
| --------------------------- | --------------------------------------------------------------------- |
| **Dependência extra**       | Nenhuma — já incluído no `resilience4j-spring-boot3` 2.3.0            |
| **Compatibilidade Java 25** | Sim — requer Java 17+, forward-compatible                             |
| **Integração Spring Boot**  | Nativa via `@RateLimiter` annotation + auto-config                    |
| **Configuração**            | Declarativa em `application.yml` (mesma estrutura do circuit breaker) |
| **Métricas**                | Automáticas no Actuator/Prometheus (já configurado)                   |
| **Granularidade**           | Por método (annotation no controller/service)                         |
| **Algoritmo**               | Semaphore-based com refresh period                                    |

Configuração em `application.yml`:

```yaml
resilience4j:
  ratelimiter:
    configs:
      default:
        register-health-indicator: true
        limit-for-period: 60
        limit-refresh-period: 1m
        timeout-duration: 0s
    instances:
      api-default:
        base-config: default
      api-search:
        base-config: default
        limit-for-period: 30
```

Uso no controller (exemplo):

```java
@RateLimiter(name = "api-default")
@PostMapping
public ResponseEntity<UserResponseDTO> createUser(...) { ... }
```

**Prós:**
- Zero dependências novas.
- Consistência com o pattern de resiliência já adotado (circuit breaker e retry usam a mesma lib).
- Métricas no Grafana sem config adicional (`resilience4j_ratelimiter_*`).
- Health indicator automático no Actuator.
- Testável com `resilience4j-test` (já no `pom.xml`).

**Contras:**
- Rate limit é por instância da aplicação (não distribuído) — ok para single instance.
- Granularidade por método, não por usuário (chave fixa). Para limitar por `publicId`, é
  necessário um `RateLimiterRegistry` programático no interceptor em vez de annotations.
- O algoritmo semaphore-based é mais simples que token bucket (sem burst allowance).

##### Opção B — Bucket4j Core standalone

Biblioteca dedicada a rate limiting usando algoritmo token bucket (RFC 6585 friendly).
Versão mais recente: **8.16.1** (fevereiro 2026). O artifact `bucket4j_jdk17-core` requer
JDK 17+, forward-compatible com Java 25.

| Aspecto                     | Detalhe                                                     |
| --------------------------- | ----------------------------------------------------------- |
| **Dependência extra**       | 1 nova: `com.bucket4j:bucket4j_jdk17-core:8.16.1`           |
| **Compatibilidade Java 25** | Sim — artifact JDK 17, forward-compatible                   |
| **Integração Spring Boot**  | Manual via `HandlerInterceptor` ou `Filter`                 |
| **Configuração**            | Programática (sem auto-config)                              |
| **Métricas**                | Manual (listener API para emitir para Micrometer)           |
| **Granularidade**           | Por chave arbitrária (IP, userId, API key) — muito flexível |
| **Algoritmo**               | Token bucket com suporte a burst                            |

Uso manual com interceptor (exemplo conceitual):

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<UUID, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        UUID publicId = extractPublicId(request);
        Bucket bucket = buckets.computeIfAbsent(publicId, this::createBucket);

        if (bucket.tryConsume(1)) {
            return true;
        }
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        return false;
    }

    private Bucket createBucket(UUID key) {
        return Bucket.builder()
                .addLimit(BandwidthBuilder.builder()
                        .capacity(60)
                        .refillGreedy(60, Duration.ofMinutes(1))
                        .build())
                .build();
    }
}
```

**Prós:**
- Algoritmo token bucket nativo — suporta burst (um usuário pode usar tokens acumulados).
- Granularidade total: rate limit por `publicId` do JWT, naturalmente.
- Bucket por usuário com `ConcurrentHashMap` + Caffeine para eviction.
- Biblioteca madura (2.7k stars, mantida ativamente).

**Contras:**
- Dependência nova (mas é leve, sem transitivas pesadas).
- Integração manual: interceptor, métricas, error response — tudo custom.
- Sem auto-config para `application.yml` (a menos que se adicione o `bucket4j-spring-boot-starter`,
  que é de terceiro e teve issues de compatibilidade com Spring Boot 3.4+).
- Limpar buckets expirados requer Caffeine wrapper manual.

**Nota sobre `bucket4j-spring-boot-starter`:** A versão mais recente é 0.13.0, mantida por
terceiro (MarcGiffing). Houve issues de compatibilidade com Spring Boot 3.4.2 (corrigido em
0.12.9). Para Spring Boot 3.5.11, a compatibilidade não está confirmada. Adicionar o starter
traz auto-config via `application.yml`, mas também traz complexidade de config (filter chains,
cache provider, etc.). **Não recomendado** — o core standalone é mais seguro e previsível.

##### Opção C — Filter manual sem biblioteca

Implementar rate limiting com `ConcurrentHashMap<UUID, AtomicInteger>` + `@Scheduled` para
reset periódico, sem nenhuma biblioteca externa.

**Prós:** Zero dependências.
**Contras:** Reinventar a roda. Sem algoritmo token bucket (burst). Sem métricas. Sem thread
safety robusta para edge cases. Não recomendado.

#### Decisão recomendada

| Critério                         | Resilience4j `@RateLimiter` | Bucket4j Core |
| -------------------------------- | :-------------------------: | :-----------: |
| Zero dependências novas          |           **Sim**           |      Não      |
| Consistência com stack existente |           **Sim**           |      Não      |
| Rate limit por usuário (JWT)     |     Requer código extra     |  **Nativo**   |
| Algoritmo token bucket (burst)   |             Não             |    **Sim**    |
| Métricas automáticas             |           **Sim**           |    Manual     |
| Configuração em YAML             |           **Sim**           |      Não      |
| Esforço de implementação         |          **Baixo**          |     Médio     |

**Recomendação: Opção A (Resilience4j)** como ponto de partida.

Justificativa:
1. Já está no projeto — zero dependências, zero risco de incompatibilidade com Java 25.
2. Configuração declarativa em `application.yml` — consistente com circuit breaker e retry.
3. Métricas no Prometheus/Grafana automaticamente.
4. Para a escala atual (API de uso pessoal/time pequeno), rate limit global por endpoint é
   suficiente — não precisa ser por `publicId` individualmente.
5. Se no futuro precisar de rate limit por usuário com burst, migrar para Bucket4j Core é
   incremental (troca interceptor, mantém response format).

#### Implementação sugerida (Opção A)

**1. Configuração em `application.yml`:**

```yaml
resilience4j:
  ratelimiter:
    configs:
      default:
        register-health-indicator: true
        limit-for-period: 60
        limit-refresh-period: 1m
        timeout-duration: 0s
    instances:
      user-api:
        base-config: default
        limit-for-period: 30
      waterintake-api:
        base-config: default
        limit-for-period: 60
      waterintake-search:
        base-config: default
        limit-for-period: 20
```

**2. Annotations nos controllers:**

- `@RateLimiter(name = "user-api")` nos 4 endpoints de `UserControllerV1`
- `@RateLimiter(name = "waterintake-api")` nos endpoints CRUD de `WaterIntakeControllerV1`
- `@RateLimiter(name = "waterintake-search")` no endpoint de search (mais restritivo)

**3. Handler para `RequestNotPermitted` no `GlobalExceptionHandler`:**

```java
@ExceptionHandler(RequestNotPermitted.class)
public ResponseEntity<Object> handleRateLimitExceeded(
        RequestNotPermitted ex, WebRequest request) {
    ProblemDetail problemDetail = buildProblemDetail(
            HttpStatus.TOO_MANY_REQUESTS,
            "exception.rate-limit-exceeded",
            "rate-limit-exceeded");
    problemDetail.setProperty("retryAfterSeconds", 60);
    return handleExceptionInternal(
            ex, problemDetail, new HttpHeaders(),
            HttpStatus.TOO_MANY_REQUESTS, request);
}
```

**4. Message em `messages.properties`:**

```properties
exception.rate-limit-exceeded=Too many requests. Please wait before trying again.
```

**5. Actuator exposure** (já está habilitado para Prometheus — as métricas
`resilience4j_ratelimiter_*` aparecem automaticamente).

**Arquivos impactados:** 0 dependências novas, 3 ajustes (`application.yml`,
`GlobalExceptionHandler`, `messages.properties`), 2 controllers anotados, testes novos.

#### Evolução futura (se necessário)

Se a API escalar para múltiplos usuários simultâneos e rate limit global por endpoint se
tornar insuficiente, migrar para **Bucket4j Core** (`bucket4j_jdk17-core:8.16.1`) com:
- `HandlerInterceptor` que extrai `publicId` do JWT
- `Caffeine<UUID, Bucket>` para armazenar buckets por usuário com eviction automática
- Métricas manuais via Micrometer `Counter` e `Timer`

Essa migração é incremental e não quebra a resposta 429 já definida.

---

### 2. Testes de Validação do WaterIntakeDTO e WaterIntakeFilterDTO — Prioridade Média-Baixa · Esforço Baixo [DONE]

**Situação atual:** A maioria dos cenários de validação via `@WebMvcTest` já está coberta:

- `UserDTOTest` — email null, blank, formato inválido, pattern inválido, tamanho excedido,
  personal/physical/settings null.
- `BirthDateValidatorTest` — data futura, null, boundary 13 anos, boundary 99 anos.
- `AlarmTimeValidatorTest` — start antes das 06:00, end após 22:00, start >= end, ambos inválidos
  (POST e PUT).
- `DateRangeValidatorTest` — endDate antes de startDate, endDate igual a startDate.
- `VolumeRangeValidatorTest` — maxVolume < minVolume, maxVolume igual, maxVolume maior.

**O que falta testar (5 cenários no `WaterIntakeControllerV1`):**

- `WaterIntakeDTO`: `dateTimeUTC` no futuro (`@PastOrPresent`), volume <= 0 (`@Positive`),
  `volumeUnit` null (`@NotNull`).
- `WaterIntakeFilterDTO` (search): `sortField` inválido (`@Pattern`), `size` fora do range 1–50
  (`@Range`).
- Todos devem verificar resposta `400` com estrutura `ProblemDetail` + array `errors`.

**Arquivos impactados:** 1 novo (`WaterIntakeDTOValidationTest`) ou 5 testes adicionados em
`WaterIntakeControllerTest`.

---

### 3. Auditoria (created_at / updated_at) — Prioridade Média · Esforço Baixo

**Problema:** As tabelas `users`, `alarm_settings` e `water_intakes` não possuem colunas de
auditoria. Não há como saber quando um registro foi criado ou modificado pela última vez.
Isso dificulta troubleshooting, análise de comportamento do usuário e eventual compliance.

#### Escopo das tabelas

| Tabela            | `created_at` | `updated_at` | Justificativa                                                        |
| ----------------- | :----------: | :----------: | -------------------------------------------------------------------- |
| `users`           |     Sim      |     Sim      | Aggregate root; criação e atualizações de perfil são relevantes      |
| `alarm_settings`  |     Não      |     Não      | Child entity de `User`; atualizado junto com o aggregate via CASCADE |
| `water_intakes`   |     Sim      |     Não      | Registros imutáveis — uma vez criados, são substituídos (PUT), nunca editados parcialmente. `created_at` permite rastrear quando o registro entrou no sistema |

`alarm_settings` não precisa de auditoria própria porque faz parte do aggregate `User` em
Spring Data JDBC — é salvo e carregado junto com o root. O `updated_at` do `User` já cobre
mudanças nos settings. `water_intakes` não precisa de `updated_at` porque o PUT faz delete +
insert (comportamento do Spring Data JDBC para entidades com `@Id` explícito e save); o
`created_at` captura o momento de inserção.

#### Análise de alternativas

Três opções viáveis para o stack atual (Java 25, Spring Data JDBC, Spring Boot 3.5.11):

##### Opção A — `@EnableJdbcAuditing` + `@CreatedDate` / `@LastModifiedDate` (RECOMENDADA)

O Spring Data JDBC suporta auditoria nativa desde a versão 2.0, com anotações do package
`org.springframework.data.annotation`. O starter `spring-boot-starter-data-jdbc` já inclui
tudo — zero dependências novas.

| Aspecto                     | Detalhe                                                                              |
| --------------------------- | ------------------------------------------------------------------------------------ |
| **Dependência extra**       | Nenhuma — já incluído no `spring-boot-starter-data-jdbc`                             |
| **Compatibilidade Java 25** | Sim — Spring Data JDBC 3.5.x requer Java 17+, forward-compatible                    |
| **Integração Spring Boot**  | Nativa via `@EnableJdbcAuditing` + auto-config                                       |
| **Configuração**            | 1 annotation em `@Configuration` class + campos nas entidades                        |
| **Granularidade**           | Por campo individual (`@CreatedDate`, `@LastModifiedDate`)                           |
| **Mecanismo**               | `RelationalAuditingCallback` intercepta save/insert automaticamente                  |

Configuração:

```java
@Configuration
@EnableJdbcAuditing
public class JdbcAuditingConfig {
}
```

Campos na entidade `User` (exemplo):

```java
@CreatedDate
@Column("created_at")
private final Instant createdAt;

@LastModifiedDate
@Column("updated_at")
private final Instant updatedAt;
```

**Prós:**
- Zero dependências novas — tudo já está no starter.
- Consistência com o pattern Spring Data JDBC já adotado no projeto.
- `RelationalAuditingCallback` preenche os campos automaticamente antes do insert/update.
- Funciona com classes imutáveis (final fields) via wither methods ou `@PersistenceCreator`.
- Testável: basta verificar que os campos não são null após save.

**Contras:**
- Requer que os campos `createdAt`/`updatedAt` estejam no modelo Java — as entidades
  `User` e `WaterIntake` precisam de novos campos e construtores ajustados.
- O `@LastModifiedDate` é setado também no insert (não apenas no update) — o valor será
  igual ao `@CreatedDate` na criação. Isso é o comportamento padrão e aceitável.
- Entidades imutáveis (como as atuais `User` e `WaterIntake`) precisam de ajuste nos
  construtores e nos métodos `with*` para propagar os timestamps.

##### Opção B — `BeforeSaveCallback` / `BeforeConvertCallback` programático

Implementar um `BeforeSaveCallback<User>` (e análogo para `WaterIntake`) que seta os
timestamps manualmente antes de cada save.

```java
@Component
public class AuditingCallback implements BeforeSaveCallback<User> {

    @Override
    public User onBeforeSave(User aggregate, MutableAggregateChange<User> change) {
        Instant now = Instant.now();
        if (aggregate.getCreatedAt() == null) {
            return aggregate.withCreatedAt(now).withUpdatedAt(now);
        }
        return aggregate.withUpdatedAt(now);
    }
}
```

| Aspecto                     | Detalhe                                                        |
| --------------------------- | -------------------------------------------------------------- |
| **Dependência extra**       | Nenhuma                                                        |
| **Compatibilidade Java 25** | Sim                                                            |
| **Configuração**            | 1 bean `@Component` por aggregate root                         |
| **Granularidade**           | Total — lógica custom por entidade                             |
| **Mecanismo**               | Callback manual no lifecycle do Spring Data JDBC               |

**Prós:**
- Controle total sobre a lógica de timestamps (ex: não setar `updatedAt` no insert).
- Sem necessidade de `@EnableJdbcAuditing` — menos magia, mais explícito.
- Pode aplicar lógica condicional (ex: só setar `updated_at` se houve mudança real).

**Contras:**
- Mais código boilerplate — um callback por aggregate root.
- Duplicação de lógica se houver múltiplos aggregates com auditoria.
- Responsabilidade do dev lembrar de registrar o callback para cada nova entidade.
- Mais propenso a bugs (esquecer de setar `createdAt` em cenários de edge case).

##### Opção C — Triggers no PostgreSQL (`DEFAULT NOW()` + trigger para `updated_at`)

Usar `DEFAULT NOW()` para `created_at` e um trigger `BEFORE UPDATE` para `updated_at`
diretamente no banco, sem envolver o Java.

```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

**Prós:**
- Zero mudança nas entidades Java — os valores são setados pelo banco.
- Garante auditoria mesmo para operações SQL diretas (migrations, scripts de correção).
- Impossível esquecer — o trigger é declarativo e sempre executa.

**Contras:**
- Lógica de negócio no banco — vai contra o princípio do projeto de manter regras no Java.
- Os campos não estarão disponíveis no objeto Java retornado após `save()` a menos que
  se faça um re-read (o Spring Data JDBC não faz refresh automático para colunas server-side).
- Triggers são invisíveis para os testes unitários — mais difícil de testar.
- Dois pontos de verdade: Java para o model, SQL para a auditoria.
- Em Spring Data JDBC, o `save()` de um aggregate root faz DELETE + INSERT dos child
  entities — triggers de UPDATE em child tables nunca disparariam.

#### Decisão recomendada

| Critério                             | `@EnableJdbcAuditing` (A) | `BeforeSaveCallback` (B) | Trigger SQL (C)  |
| ------------------------------------ | :-----------------------: | :----------------------: | :--------------: |
| Zero dependências novas              |          **Sim**          |         **Sim**          |     **Sim**      |
| Consistência com Spring Data JDBC    |          **Sim**          |           Sim            |       Não        |
| Valores disponíveis no Java após save|          **Sim**          |         **Sim**          |       Não        |
| Código boilerplate                   |        **Mínimo**         |          Médio           |     Mínimo       |
| Controle fino sobre lógica           |           Não             |         **Sim**          |       Não        |
| Testabilidade                        |          **Sim**          |         **Sim**          |   Só integração  |
| Proteção contra SQL direto           |           Não             |           Não            |     **Sim**      |
| Esforço de implementação             |        **Baixo**          |          Médio           |      Baixo       |

**Recomendação: Opção A (`@EnableJdbcAuditing`)** como ponto de partida.

Justificativa:
1. Já está incluída no starter — zero dependências, zero risco de incompatibilidade.
2. É o mecanismo idiomático do Spring Data JDBC — consistente com o pattern do projeto.
3. Os timestamps ficam disponíveis no objeto Java imediatamente após `save()` sem re-read.
4. Para a escala atual, proteção contra SQL direto (vantagem da Opção C) não é necessária.
5. Se no futuro precisar de lógica condicional, migrar para `BeforeSaveCallback` é
   incremental (troca a annotation por callback, mantém as colunas e DTOs).

#### Implementação sugerida (Opção A)

**1. Migration `V2__add_audit_columns.sql`:**

```sql
ALTER TABLE users
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE water_intakes
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
```

Nota: `DEFAULT NOW()` preenche as linhas existentes com o momento da migration. Para registros
existentes, esse valor é uma aproximação — aceitável para um sistema em estágio inicial.

**2. Configuração `JdbcAuditingConfig`:**

```java
package br.com.drinkwater.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@Configuration
@EnableJdbcAuditing
public class JdbcAuditingConfig {
}
```

**3. Ajuste na entidade `User`:**

Adicionar os campos `createdAt` e `updatedAt` com as annotations de auditoria. Como a
entidade é imutável (campos `final`), o `@PersistenceCreator` recebe os valores do banco,
e o Spring Data preenche via wither methods ou reflexão antes do save.

```java
@CreatedDate
@Column("created_at")
private final Instant createdAt;

@LastModifiedDate
@Column("updated_at")
private final Instant updatedAt;
```

Ajustar os construtores:
- Business constructor: `createdAt` e `updatedAt` como `null` (serão setados pelo auditing).
- `@PersistenceCreator`: recebe `Instant createdAt` e `Instant updatedAt` do banco.
- Métodos `withUpdatedFields` e `withSettings`: propagar `createdAt` e `updatedAt` existentes.

**4. Ajuste na entidade `WaterIntake`:**

Adicionar apenas `createdAt`:

```java
@CreatedDate
@Column("created_at")
private final Instant createdAt;
```

Ajustar os construtores de forma análoga ao `User`.

**5. Ajuste nos response DTOs:**

```java
public record UserResponseDTO(
        UUID publicId,
        String email,
        PersonalDTO personal,
        PhysicalDTO physical,
        AlarmSettingsResponseDTO settings,
        Instant createdAt,
        Instant updatedAt) {}

public record WaterIntakeResponseDTO(
        Long id,
        Instant dateTimeUTC,
        int volume,
        VolumeUnit volumeUnit,
        Instant createdAt) {}
```

**6. Ajuste nos mappers:**

`UserMapper.toDto` — mapear `entity.getCreatedAt()` e `entity.getUpdatedAt()`.
`WaterIntakeMapper.toDto` — mapear `entity.getCreatedAt()`.

**7. Ajuste nos dados de teste:**

Os arquivos `insert-test-data.sql` e `R__test_data.sql` precisam incluir `created_at` nos
INSERTs (ou podem omitir e usar o `DEFAULT`). Recomendado: omitir e deixar o `DEFAULT NOW()`
preencher, já que os testes não dependem de timestamps de auditoria específicos.

**8. Testes:**

- **Unitário:** Verificar que os mappers incluem `createdAt`/`updatedAt` no DTO.
- **Integração:** Verificar que após `save()`, a entidade retornada tem `createdAt` não-null.
  Verificar que após update, `updatedAt` é posterior ao `createdAt`.

**Arquivos impactados:** 1 migration nova, 1 config nova (`JdbcAuditingConfig`), 2 entidades
ajustadas (`User`, `WaterIntake`), 2 response DTOs ajustados, 2 mappers ajustados, testes
existentes adaptados + 2–3 testes novos.

#### Impacto na API (breaking change)

Adicionar `createdAt` e `updatedAt` nos response DTOs é uma **mudança aditiva** — novos
campos em JSON não quebram clientes existentes (RFC 7396). Clientes que deserializam com
`@JsonIgnoreProperties(ignoreUnknown = true)` (padrão do Jackson) não são afetados.

Se por algum motivo a exposição dos timestamps for indesejada na primeira versão, basta
omitir dos DTOs e usar `@JsonIgnore` ou simplesmente não mapear — os campos ficam
apenas no modelo interno para troubleshooting via banco.

#### Evolução futura (se necessário)

- **`@CreatedBy` / `@LastModifiedBy`:** Se o sistema precisar registrar quem fez a mudança
  (além de quando), basta implementar um `AuditorAware<UUID>` que extrai o `publicId` do
  `SecurityContext` e adicionar colunas `created_by` / `updated_by` nas tabelas.
- **Event sourcing leve:** Se o histórico completo de mudanças for necessário, considerar
  uma tabela `user_audit_log` com trigger ou `ApplicationEventPublisher` no service.

---

### 4. Correlation ID nos Logs e Respostas — Prioridade Média · Esforço Baixo

**Problema:** Os logs de services e controllers não propagam um trace/correlation ID. Com o stack
Loki + Grafana já configurado, sem correlation ID perde-se a capacidade de rastrear um request
end-to-end.

**Abordagem sugerida:**

- Habilitar Micrometer Tracing (dependência `micrometer-tracing-bridge-otel` ou similar) para
  propagar `traceId` automaticamente via MDC nos logs.
- Adicionar um filtro simples que copia `traceId` para o response header `X-Trace-Id`.
- As variáveis `TRACING_*` e `ZIPKIN_ENDPOINT` já existem no `.env.example` — ativar a integração.

**Arquivos impactados:** 1–2 dependências no `pom.xml`, 1 filtro novo, ajuste no `application.yml`.

---

### 5. README Atualizado — Prioridade Média · Esforço Trivial

**Problema:** O `README.md` informa Java 17 e Spring Boot 3.4.4, enquanto o `pom.xml` está em
Java 25 e Spring Boot 3.5.11.

**Ação:** Atualizar as versões no README para refletir o `pom.xml` atual.

**Arquivos impactados:** 1 (`README.md`).

---

### 6. Fonte Única de Test Data — Prioridade Média · Esforço Trivial

**Problema:** Existem dois arquivos com dados de teste praticamente idênticos:

- `src/test/resources/insert-test-data.sql`
- `src/test/resources/db/testdata/R__test_data.sql`

Manter dois fontes de verdade cria risco de drift silencioso.

**Ação:** Consolidar em um único arquivo (preferencialmente o `R__test_data.sql` como Flyway
repeatable migration) e atualizar as referências `@Sql` nos testes de integração.

**Arquivos impactados:** 1 SQL removido, 2–3 testes ajustados.

---

### 7. ETag para GETs — Prioridade Baixa · Esforço Trivial

**Problema:** Endpoints GET não retornam `ETag` nem suportam `If-None-Match`. Para dados que mudam
pouco (perfil do usuário), isso gera tráfego desnecessário.

**Abordagem sugerida:**

- Registrar `ShallowEtagHeaderFilter` como bean (uma linha de configuração).
- O Spring calcula o ETag automaticamente com base no body da resposta.
- Zero mudança nos controllers.

**Arquivos impactados:** 1 bean de configuração novo.

---

### 8. Idempotency Key para POSTs — Prioridade Baixa · Esforço Médio

**Problema:** Os endpoints POST não suportam idempotency key. Clientes com retry automático
(mobile em rede instável) podem gerar efeitos colaterais indesejados (409 no create user,
duplicação de tentativas no create water intake).

**Abordagem sugerida:**

- Aceitar header `Idempotency-Key` opcional nos POSTs.
- Armazenar o resultado por alguns minutos em cache Caffeine (já no projeto).
- Retornar a resposta cacheada se a mesma key vier novamente.
- Implementar via `HandlerInterceptor`.

**Arquivos impactados:** 2–3 novos (interceptor, config, testes).

---

### 9. Flyway Health Check — Prioridade Baixa · Esforço Trivial

**Problema:** O Actuator verifica a saúde do DataSource automaticamente, mas não expõe o estado
das migrations Flyway. Em produção, saber se as migrations estão aplicadas é útil para
troubleshooting.

**Ação:** Habilitar no `application.yml`:

```yaml
management:
  endpoint:
    flyway:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,prometheus,flyway
```

**Arquivos impactados:** 1 (`application.yml`).
