# Roadmap de Melhorias — Drink Water API

Plano de melhorias organizado em 9 ciclos independentes, ordenados por esforço (menor para maior).
Cada ciclo pode ser implementado e commitado isoladamente.

---

## Ciclo 1 — Correções pontuais de código [DONE]

**Esforço:** ~15 min | **Risco:** Nenhum | **Arquivos:** 2

### 1.1 Renomear método inconsistente no `GlobalExceptionHandler`

**Problema:** O método que trata `UserAlreadyExistsException` se chama `handleEmailAlreadyUsedException`.
O nome é resquício de quando a exceção era sobre email duplicado.

**Evidência:**

```java
// GlobalExceptionHandler.java, linhas 95-104
@ExceptionHandler(UserAlreadyExistsException.class)
public ResponseEntity<Object> handleEmailAlreadyUsedException(   // <-- nome incorreto
        UserAlreadyExistsException ex, WebRequest request) {
    return buildResponse(
            ex,
            request,
            HttpStatus.CONFLICT,
            "exception.user.already-exists",
            "user-already-exists");
}
```

**Ação:** Renomear para `handleUserAlreadyExistsException`.

**Resultado esperado:** Consistência entre o nome do método e a exceção tratada.

---

### 1.2 Proteger `Personal` contra NPE no constructor

**Problema:** `firstName.trim().isEmpty()` lança `NullPointerException` se `firstName` for `null`,
pois `.trim()` é chamado antes de qualquer null check. A mensagem de erro diz "cannot be null or empty"
mas o null não é tratado de fato.

**Evidência:**

```java
// Personal.java, linhas 22-31
public Personal(
        String firstName, String lastName, LocalDate birthDate, BiologicalSex biologicalSex) {
    if (firstName.trim().isEmpty()) {                          // <-- NPE se firstName == null
        throw new IllegalArgumentException("First name cannot be null or empty");
    }

    if (lastName.trim().isEmpty()) {                           // <-- NPE se lastName == null
        throw new IllegalArgumentException("Last name cannot be null or empty");
    }
    // ...
}
```

**Ação:** Adicionar null check antes do `.trim()`, exemplo:
```java
if (firstName == null || firstName.trim().isEmpty()) {
    throw new IllegalArgumentException("First name cannot be null or empty");
}
```

**Resultado esperado:** Mensagem clara de `IllegalArgumentException` ao invés de `NullPointerException` genérico.

---

### 1.3 Documentar limitação do SpotBugs com Java 25

**Problema:** SpotBugs está configurado com `failOnError=false` porque não suporta Java 25.
O plugin roda sem quebrar o build, mas não reporta bugs reais — é uma falsa sensação de segurança.

**Evidência:**

```xml
<!-- pom.xml, linhas 377-396 -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>${spotbugs-maven-plugin.version}</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Medium</threshold>
        <failOnError>false</failOnError>   <!-- mascarando incompatibilidade -->
        <excludeFilterFile>config/spotbugs/spotbugs-exclude.xml</excludeFilterFile>
    </configuration>
</plugin>
```

**Ação:** Adicionar comentário XML explicando a limitação e criar issue para reabilitar quando suporte existir.

**Resultado esperado:** Qualquer pessoa que ler o `pom.xml` entende que SpotBugs está parcialmente desabilitado e por quê.

---

## Ciclo 2 — Docker Compose local funcional [DONE]

**Esforço:** ~30 min | **Risco:** Baixo | **Arquivos:** 1-2

### 2.1 Descomentar e ajustar `drinkwater_api` no `docker-compose.yml`

**Problema:** O serviço da aplicação está completamente comentado. `docker compose up` sobe apenas
PostgreSQL e Keycloak, mas não a API.

**Evidência:**

```yaml
# docker-compose.yml, linhas 161-268 — serviço inteiro comentado
#  drinkwater_api:
#    container_name: drinkwater_api
#    image: eduardokohn/drinkwaterapi:latest
#    restart: ${RESTART_POLICY}
#    env_file:
#      - .env
#    ...
```

**Ação:**
- Descomentar o serviço
- Adicionar defaults com `${VAR:-default}` (como os demais serviços)
- Remover variáveis obsoletas (ex: `JPA_*` — o projeto usa Spring Data JDBC)
- Validar com `docker compose config`

**Resultado esperado:** `docker compose up` sobe todo o stack local (DB + Keycloak + API) sem configuração extra.

---

## Ciclo 3 — Logging no domínio [DONE]

**Esforço:** ~1h | **Risco:** Nenhum | **Arquivos:** 4-6

### 3.1 Adicionar logging em services e controllers

**Problema:** `UserService`, `WaterIntakeService`, `UserControllerV1` e `WaterIntakeControllerV1`
não possuem nenhum logging. Em produção, não há como rastrear operações de negócio.

**Evidência — `UserService`:**

```java
// UserService.java — nenhum import de Logger, nenhum log em 71 linhas
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UUID publicId, UserDTO userDTO) {
        this.validateUserExistence(publicId);            // sem log de tentativa de criação
        User userEntity = this.userMapper.toEntity(userDTO, publicId);
        User savedUser = this.userRepository.save(userEntity);
        return this.userMapper.toDto(savedUser);         // sem log de sucesso
    }
    // ...
}
```

**Evidência — `WaterIntakeService`:**

```java
// WaterIntakeService.java — nenhum import de Logger em 151 linhas
@Service
public class WaterIntakeService {
    // ... 5 dependências injetadas, 0 Logger
}
```

**Evidência — `UserControllerV1`:**

```java
// UserControllerV1.java — nenhum logging em 56 linhas
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    // CRUD completo sem nenhum log
}
```

**Ação:**
- Adicionar `private static final Logger log = LoggerFactory.getLogger(...)` nos 4 arquivos
- Log de INFO em operações de escrita (create, update, delete) com IDs relevantes
- Log de DEBUG em operações de leitura
- Log de WARN/ERROR em exceções de negócio

**Resultado esperado:** Rastreabilidade completa de operações de negócio em produção.

---

## Ciclo 4 — Observabilidade (métricas + logs + health check) [DONE]

**Esforço:** ~3-4h | **Risco:** Baixo | **Arquivos:** 5-8

### Stack de observabilidade (100% open-source)

| Camada                          | Ferramenta | Licença    | Função                                   |
| ------------------------------- | ---------- | ---------- | ---------------------------------------- |
| Métricas (instrumentação)       | Micrometer | Apache 2.0 | Já integrado no Spring Boot Actuator     |
| Métricas (coleta/armazenamento) | Prometheus | Apache 2.0 | Scrape de `/actuator/prometheus`         |
| Logs (coleta)                   | Promtail   | Apache 2.0 | Agente que coleta logs dos containers    |
| Logs (armazenamento)            | Loki       | AGPL 3.0   | Armazena logs indexando por labels       |
| Visualização                    | Grafana    | AGPL 3.0   | Dashboards unificados de métricas e logs |

Nota: Elasticsearch/Kibana (stack ELK) **não são open-source** desde 2021 (mudaram para SSPL/Elastic License).
Loki é a alternativa open-source para logs, mais leve e integrada ao Grafana.

Nota: Na implementação, usar as versões mais recentes de cada ferramenta desde que compatíveis
com a versão de Java e Spring Boot do projeto. Prometheus, Loki, Promtail e Grafana rodam em
containers separados — usar sempre as **imagens oficiais** do Docker Hub (`prom/prometheus`,
`grafana/loki`, `grafana/promtail`, `grafana/grafana`).

### 4.1 Métricas customizadas de negócio

**Problema:** Micrometer/Prometheus estão configurados, mas só exportam métricas genéricas do framework.
Não há contadores de negócio.

**Evidência:**

```xml
<!-- pom.xml — dependência existe -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

Mas nenhum service injeta `MeterRegistry` ou registra métricas customizadas.

**Ação:**
- Injetar `MeterRegistry` em `UserService` e `WaterIntakeService`
- Adicionar contadores: `users.created`, `users.deleted`, `water_intakes.created`, `water_intakes.deleted`
- Adicionar timer para `waterIntakeSearchRepository.search`

**Resultado esperado:** Dashboards Grafana com métricas de negócio reais.

---

### 4.2 Infraestrutura de coleta (Prometheus + Loki + Grafana)

**Problema:** Mesmo com o endpoint `/actuator/prometheus` expondo métricas, não há nenhum serviço
coletando ou visualizando esses dados. Logs ficam apenas no stdout dos containers.

**Ação:**
- Adicionar ao `docker-compose.yml` os serviços: `prometheus`, `loki`, `promtail`, `grafana`
- Criar `config/prometheus/prometheus.yml` com scrape config apontando para a API
- Criar `config/promtail/promtail.yml` para coletar logs dos containers Docker
- Criar `config/loki/loki.yml` com configuração de armazenamento local
- Configurar datasources do Grafana (Prometheus + Loki) via provisioning

**Resultado esperado:** `docker compose up` sobe todo o stack de observabilidade.
Grafana acessível em `http://localhost:3000` com métricas e logs unificados.

---

### 4.3 Health check customizado para Keycloak

**Problema:** O Actuator reporta health do banco de dados automaticamente, mas não verifica
se o Keycloak (dependência crítica de autenticação) está acessível.

**Ação:**
- Criar classe `KeycloakHealthIndicator implements HealthIndicator`
- Verificar conectividade com o endpoint de well-known do Keycloak
- Registrar como `@Component`

**Resultado esperado:** `/actuator/health` mostra status do Keycloak, útil para readiness probes.

---

## Ciclo 5 — Cache [DONE]

**Esforço:** ~2h | **Risco:** Baixo | **Arquivos:** 4-5

### 5.1 Cache local com Caffeine para resolução de `userId`

**Problema:** Toda requisição de `WaterIntake` chama `UserService.findByPublicId` para resolver
o `userId` interno. Essa consulta executa uma query ao banco a cada request do mesmo usuário.

**Evidência:**

```java
// WaterIntakeService.java, linhas 176-179
private Long resolveUserId(UUID publicId) {
    log.debug("Resolving userId for publicId: {}", publicId);
    var user = this.userService.findByPublicId(publicId);   // query ao banco toda vez
    return Objects.requireNonNull(user.getId(), "User must have an ID");
}
```

Chamado em: `create`, `update`, `findByIdAndUserId`, `deleteByIdAndUserId`, `search` — 5 métodos.

**Por que Caffeine (e não cache distribuído):**
A aplicação roda como instância única. O dado cacheado é um mapeamento imutável
`UUID publicId → Long userId` (~80-100 bytes por entrada). Para 10.000 usuários ativos,
o cache consome ~1 MB de heap. Cache distribuído (Valkey, Dragonfly, KeyDB) adiciona latência
de rede e complexidade operacional sem benefício neste cenário. Se futuramente a API escalar
para múltiplas instâncias, Valkey (fork open-source do Redis, BSD 3-Clause, Linux Foundation)
é o caminho de migração recomendado — compatível com `spring-boot-starter-data-redis` via Lettuce.

**Decisão de design — NÃO cachear `findByPublicId` diretamente:**
O método `findByPublicId` retorna a entidade `User` completa e é usado por `getUserByPublicId`
(que precisa de dados atualizados após edição) e `updateUser` (que precisa da versão mais
recente para merge). Cachear esse método causaria dados stale no GET/PUT do usuário.
A solução correta é criar um método dedicado que retorna apenas o `Long userId`, dado que
o mapeamento `publicId → userId` é imutável (o ID interno nunca muda).

**Premissa:** Spring Cache opera via proxy. Chamadas internas na mesma classe bypassam o cache.
Como `WaterIntakeService` chama `userService.resolveUserIdByPublicId()` (outra classe),
o proxy intercepta corretamente. Dentro do `resolveUserIdByPublicId`, a chamada a
`this.findByPublicId()` é interna e não passa pelo proxy — isso é intencional, pois
o `@Cacheable` está no método externo.

**Ação:**

1. Adicionar dependências ao `pom.xml`:
   - `spring-boot-starter-cache`
   - `com.github.ben-manes.caffeine:caffeine`

2. Criar `CacheConfig` com configuração robusta:
   ```java
   @Configuration
   @EnableCaching
   public class CacheConfig {

       @Bean
       public CacheManager cacheManager() {
           var caffeine = Caffeine.newBuilder()
                   .maximumSize(10_000)
                   .expireAfterWrite(Duration.ofMinutes(5))
                   .recordStats();

           var manager = new CaffeineCacheManager("userIdByPublicId");
           manager.setCaffeine(caffeine);
           return manager;
       }
   }
   ```
   - `maximumSize(10_000)` — eviction automática via Window TinyLFU quando atingir o limite
   - `expireAfterWrite(5min)` — TTL para evitar dados stale em cenários extremos
   - `recordStats()` — expõe hit/miss/eviction via Micrometer (já integrado no projeto)

3. Criar método dedicado em `UserService`:
   ```java
   @Cacheable("userIdByPublicId")
   @Transactional(readOnly = true)
   public Long resolveUserIdByPublicId(UUID publicId) {
       return this.findByPublicId(publicId).getId();
   }
   ```

4. Atualizar `WaterIntakeService.resolveUserId` para chamar o novo método:
   ```java
   private Long resolveUserId(UUID publicId) {
       return this.userService.resolveUserIdByPublicId(publicId);
   }
   ```

5. Adicionar eviction em `UserService.deleteByPublicId`:
   ```java
   @CacheEvict(value = "userIdByPublicId", key = "#publicId")
   @Transactional
   public void deleteByPublicId(UUID publicId) { ... }
   ```
   Nota: `updateUser` NÃO precisa de `@CacheEvict` porque o mapeamento `publicId → userId`
   é imutável — atualizar o perfil do usuário não altera o ID interno.

**Resultado esperado:** Eliminação de queries repetidas ao banco para resolução de `userId`.
Cache auto-contido com limite de memória (~1 MB para 10.000 entradas), eviction automática,
TTL, e métricas de hit/miss visíveis no Grafana via Micrometer.

---

## Ciclo 6 — Javadoc [DONE]

**Esforço:** ~4-5h | **Risco:** Zero funcional | **Arquivos:** ~35

**Estado atual:** Classes de configuração/runtime (`RuntimeConfigurationController`,
`RuntimeConfigurationService`, `RuntimeConfigurationValidator`, `RuntimeLoggingConfiguration`,
`RuntimeMonitoringConfiguration`, `RuntimeActuatorConfiguration`, `EnvironmentVariableValidator`,
`ValidationErrorFormatter`, `ApplicationProperties`) já possuem Javadoc completo.
Métodos de delete idempotentes (`UserService.deleteByPublicId`, `WaterIntakeService.deleteByIdAndUserId`)
e `KeycloakWebhookController.handleKeycloakEvent` também já possuem Javadoc parcial.
Todo o restante do domínio está sem documentação.

### 6.1 Controllers (prioridade alta — ponto de entrada da API)

**Problema:** 3 dos 4 controllers não possuem nenhum Javadoc. `KeycloakWebhookController` documenta
apenas o método `handleKeycloakEvent`.

**Evidência:**

```java
// UserControllerV1.java — 0 Javadoc em 4 endpoints
@GetMapping("/me")
public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticatedUser UUID publicId) { ... }

@PostMapping
public ResponseEntity<UserResponseDTO> createUser(...) { ... }
```

```java
// WaterIntakeControllerV1.java — 0 Javadoc em 5 endpoints
@PostMapping
public ResponseEntity<WaterIntakeResponseDTO> create(...) { ... }

@GetMapping
public ResponseEntity<CursorPageResponse<WaterIntakeResponseDTO>> search(...) { ... }
```

**Ação:**
- `UserControllerV1` — class-level + 4 métodos (GET, POST, PUT, DELETE)
- `WaterIntakeControllerV1` — class-level + 5 métodos (POST, GET search, GET by id, PUT, DELETE)
- `KeycloakWebhookController` — class-level (método já documentado)
- Foco: propósito do endpoint, parâmetros, códigos HTTP retornados, exceções

### 6.2 Services (prioridade alta — lógica de negócio)

**Problema:** `UserService` e `WaterIntakeService` documentam apenas seus respectivos métodos de
delete idempotente. Restante dos métodos públicos sem Javadoc.

**Ação:**
- `UserService` — class-level + métodos públicos não documentados (`findByPublicId`,
  `createUser`, `updateUser`, `resolveUserIdByPublicId`)
- `WaterIntakeService` — class-level + métodos públicos não documentados (`create`, `search`,
  `findByIdAndUserId`, `update`). Documentar a resolução de `userId` via `publicId` e o
  padrão de cursor-based pagination no `search`

### 6.3 Repositories (prioridade média)

**Problema:** Nenhum repository possui Javadoc. `WaterIntakeSearchRepository` e sua implementação
`WaterIntakeSearchRepositoryImpl` contêm lógica de query dinâmica com cursor-based pagination
que não é óbvia pela assinatura.

**Ação:**
- `UserRepository` — class-level + métodos custom
- `WaterIntakeRepository` — class-level + métodos custom
- `WaterIntakeSearchRepository` — class-level + contrato da interface de busca
- `WaterIntakeSearchRepositoryImpl` — class-level + documentar a estratégia de paginação por cursor

### 6.4 Models e Enums (prioridade média — lógica de negócio nos construtores)

**Problema:** 9 classes de modelo/enum sem Javadoc. Modelos como `Personal`, `Physical`,
`AlarmSettings` e `WaterIntake` possuem validações de negócio nos construtores que não são
óbvias pela assinatura. Enums `VolumeUnit`, `BiologicalSex`, `HeightUnit` e `WeightUnit`
encapsulam lógica de conversão.

**Ação:**
- `User`, `Personal`, `Physical`, `AlarmSettings`, `WaterIntake` — class-level + documentar
  invariantes de negócio nos construtores (ex: `Personal` valida nome não-vazio,
  `AlarmSettings` valida intervalo de horas)
- `VolumeUnit`, `BiologicalSex`, `HeightUnit`, `WeightUnit` — class-level + documentar
  valores suportados e métodos de conversão

### 6.5 DTOs (prioridade baixa — records auto-descritivos)

**Problema:** 10 DTOs (todos records) sem Javadoc. A maioria é auto-descritiva pela nomenclatura
e validações via annotations, mas alguns têm semântica não-óbvia.

**Ação:**
- DTOs com semântica não-óbvia (class-level): `WaterIntakeFilterDTO` (parâmetros de busca com
  cursor pagination), `KeycloakEventDTO` (estrutura do webhook), `CursorPageResponse`
  (contrato de paginação por cursor)
- DTOs simples (class-level minimal): `UserDTO`, `UserResponseDTO`, `PersonalDTO`, `PhysicalDTO`,
  `AlarmSettingsDTO`, `AlarmSettingsResponseDTO`, `WaterIntakeDTO`, `WaterIntakeResponseDTO`

### 6.6 Custom Validators (prioridade média — regras de negócio)

**Problema:** 4 annotations de validação e 4 implementações sem Javadoc. As regras de negócio
(ex: data de nascimento válida, intervalo de alarme, range de volume) ficam ocultas na implementação.

**Ação:**
- Annotations (`@ValidBirthDate`, `@ValidAlarmTime`, `@ValidDateRange`, `@ValidVolumeRange`)
  — documentar a regra de negócio que cada uma aplica
- Implementações (`BirthDateValidator`, `AlarmTimeValidator`, `DateRangeValidator`,
  `VolumeRangeValidator`) — class-level + `isValid` com condições de rejeição

### 6.7 Mappers (prioridade baixa — código de mapeamento direto)

**Problema:** 5 mappers sem Javadoc. Mapeamento é geralmente direto (DTO ↔ Entity), mas
exceções e tratamento de null merecem documentação.

**Ação:**
- `UserMapper`, `PersonalMapper`, `PhysicalMapper`, `AlarmSettingsMapper`, `WaterIntakeMapper`
  — class-level + documentar comportamento em caso de input null e direção do mapeamento

**Resultado esperado:** Cobertura de Javadoc em todas as camadas do domínio. Qualquer novo
desenvolvedor entende a API, regras de negócio e fluxo de dados sem ler a implementação.
Classes de configuração/runtime mantêm o Javadoc existente inalterado.

---

## Ciclo 7 — Cobertura de testes (JaCoCo → 100% line)

> **Nota:** Pitest (mutation testing) foi removido temporariamente do projeto.
> O Pitest 1.22.1 não é estável com JDK 25 — a CI oficial testa apenas JDK 11/18/21,
> e há issues abertas ([#1435](https://github.com/hcoles/pitest/issues/1435)) reportando
> crashes de minion (`RUN_ERROR`, `EOFException`) com JDK 25 + records + jakarta.validation.
> Reavaliar quando o Pitest publicar uma versão com suporte oficial a JDK 25.

**Estratégia:** Duas fases sequenciais, ambas executadas **arquivo por arquivo** (nunca em batch).
Cada arquivo é um mini-ciclo completo: rodar relatório → analisar gaps → escrever testes → confirmar
100% antes de avançar para o próximo arquivo.

**Risco:** Nenhum (somente arquivos de teste são criados/alterados)

### 7.0 Preparação — Ajustar JaCoCo e configurar Pitest [DONE]

**Problema:** A configuração atual do JaCoCo exclui `br/com/drinkwater/config/**/*`, o que impede
a medição de cobertura em toda a camada de configuração. Além disso, o Pitest exclui
`br.com.drinkwater.config.*` (wildcard simples), mas isso não alcança sub-pacotes como
`config.runtime.*` ou `config.security.*`, gerando inconsistência.

**Evidência — JaCoCo (pom.xml, linhas 307-311):**

```xml
<excludes>
    <exclude>br/com/drinkwater/DrinkWaterApiApplication.class</exclude>
    <exclude>br/com/drinkwater/config/**/*</exclude>
</excludes>
```

**Evidência — Pitest (pom.xml, linhas 422-425):**

```xml
<excludedClasses>
    <param>br.com.drinkwater.config.*</param>
    <param>br.com.drinkwater.DrinkWaterApiApplication</param>
</excludedClasses>
```

**Ação:**

1. **Remover** o exclude global `br/com/drinkwater/config/**/*` do JaCoCo
2. **Substituir** por excludes cirúrgicos apenas para classes que são pure wiring sem lógica
   testável (beans `@Configuration` que só fazem `new` e `return`):

```xml
<!-- JaCoCo excludes -->
<excludes>
    <exclude>br/com/drinkwater/DrinkWaterApiApplication.class</exclude>
    <exclude>br/com/drinkwater/config/DateTimeConfig.class</exclude>
    <exclude>br/com/drinkwater/config/LocaleConfig.class</exclude>
    <exclude>br/com/drinkwater/config/MessageSourceConfig.class</exclude>
    <exclude>br/com/drinkwater/config/ValidationConfig.class</exclude>
    <exclude>br/com/drinkwater/config/WebConfig.class</exclude>
    <exclude>br/com/drinkwater/config/health/HealthClientConfig.class</exclude>
    <exclude>br/com/drinkwater/config/KeycloakAdminClientProducer.class</exclude>
</excludes>
```

3. **Alinhar** os excludes do Pitest com a mesma lista (usar `**` para sub-pacotes):

```xml
<!-- Pitest excludedClasses -->
<excludedClasses>
    <param>br.com.drinkwater.DrinkWaterApiApplication</param>
    <param>br.com.drinkwater.config.DateTimeConfig</param>
    <param>br.com.drinkwater.config.LocaleConfig</param>
    <param>br.com.drinkwater.config.MessageSourceConfig</param>
    <param>br.com.drinkwater.config.ValidationConfig</param>
    <param>br.com.drinkwater.config.WebConfig</param>
    <param>br.com.drinkwater.config.health.HealthClientConfig</param>
    <param>br.com.drinkwater.config.KeycloakAdminClientProducer</param>
</excludedClasses>
```

4. Confirmar que `mvn verify -DskipTests` compila sem erros e que `mvn test jacoco:report`
   gera relatório em `target/site/jacoco/index.html`

**Resultado esperado:** JaCoCo e Pitest medem as mesmas classes. Classes de configuração pura
ficam fora, classes com lógica (security, runtime, validation, properties com compact constructors)
ficam dentro.

---

### 7.1 Fase 1 — JaCoCo line coverage → 100% (arquivo por arquivo)

**Método por arquivo:**

1. Rodar `mvn test jacoco:report`
2. Abrir `target/site/jacoco/index.html`, localizar o arquivo-alvo
3. Identificar linhas não cobertas (vermelhas) e parcialmente cobertas (amarelas)
4. Escrever testes que cubram exatamente essas linhas
5. Re-rodar `mvn test jacoco:report` e confirmar 100% line coverage para aquele arquivo
6. Commit: `test: achieve 100% line coverage for <ClassName>`
7. Passar para o próximo arquivo

**Ordem de execução:** Os arquivos estão ordenados do mais simples ao mais complexo dentro de cada
grupo. Arquivos que já possuem testes mas estão abaixo de 100% vêm primeiro (ajustar é mais rápido
que criar do zero).

#### Grupo A — Core e Exception (fora de `config/`)

Estes arquivos **nunca** estiveram no exclude do JaCoCo, então o relatório já mostra a cobertura
atual. Começar por aqui porque são os arquivos mais impactantes para o domínio.

| Passo | Arquivo fonte                                     | Arquivo de teste                                             | Tipo        | Detalhes                                                                                                                                                                                                                                                                                                                                                    |
| ----- | ------------------------------------------------- | ------------------------------------------------------------ | ----------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| A1    | `core/MessageResolver.java`                       | `core/MessageResolverTest.java` (novo)                       | Unit        | 2 overloads de `resolve()` + `requireNonNull` no construtor. Testar: key válida, key com args, `messageSource` null no construtor                                                                                                                                                                                                                           |
| A2    | `core/CodedEnum.java`                             | `core/CodedEnumTest.java` (novo)                             | Unit        | `buildLookupMap()` com enum de teste local. Testar: mapa correto, enum vazio                                                                                                                                                                                                                                                                                |
| A3    | `exception/GlobalExceptionHandler.java`           | `exception/GlobalExceptionHandlerTest.java` (novo)           | @WebMvcTest | 14 handlers: 9 `@ExceptionHandler` + 3 overrides + métodos privados `buildProblemDetail`, `extractFieldName`. **Branches críticas:** switch L162-181 (3 arms: `InvalidFormatException` / `null` / `Throwable`), `typeMismatch` branch L212, `invalidValue != null` L256 e L286, `requiredType != null` L277, `extractFieldName` com `lastDotIndex > 0` L341 |
| A4    | `hydrationtracking/dto/WaterIntakeFilterDTO.java` | `hydrationtracking/dto/WaterIntakeFilterDTOTest.java` (novo) | Unit        | Compact constructor com defaults: `size` null→10, `sortField` null→`dateTimeUTC`, `sortDirection` null→`DESC`, `sortField` inválido→fallback. Testar cada branch do compact constructor                                                                                                                                                                     |

#### Grupo B — Config com lógica (entram no JaCoCo após 7.0)

Após ajustar os excludes, estes arquivos passam a aparecer no relatório JaCoCo.

| Passo | Arquivo fonte                                            | Arquivo de teste                                                    | Tipo                          | Detalhes                                                                                                                                                                                                                                                                                                                                   |
| ----- | -------------------------------------------------------- | ------------------------------------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| B1    | `config/WebhookSecurityFilter.java`                      | `config/WebhookSecurityFilterTest.java` (novo)                      | Unit (MockHttpServletRequest) | `doFilterInternal`: secret válido→continua, secret inválido→401, secret ausente→401. `shouldNotFilter`: paths `/internal/`→false, outros→true                                                                                                                                                                                              |
| B2    | `config/SecurityConfig.java`                             | `config/SecurityConfigTest.java` (novo)                             | @SpringBootTest/@WebMvcTest   | 3 regras: actuator endpoints→permitAll, `/internal/**`→permitAll (protegido por WebhookSecurityFilter), `anyRequest()`→authenticated. **Cenários:** actuator sem token→200, API sem token→401, API com JWT válido→200, `/internal/` sem secret→401, `/internal/` com secret→200, method-level `@PreAuthorize` nos runtime-config endpoints |
| B3    | `config/CorsConfig.java`                                 | `config/CorsConfigTest.java` (novo)                                 | Integração                    | Bean wiring de `CorsFilter` com `CorsProperties`. Testar: preflight OPTIONS retorna headers corretos, origin não-permitida é rejeitada                                                                                                                                                                                                     |
| B4    | `config/security/AuthenticatedUserArgumentResolver.java` | `config/security/AuthenticatedUserArgumentResolverTest.java` (novo) | Unit                          | `supportsParameter()` e `resolveArgument()` com JWT mockado                                                                                                                                                                                                                                                                                |
| B5    | `config/EnvironmentVariableConfiguration.java`           | `config/EnvironmentVariableConfigurationTest.java` (novo)           | Unit                          | Bean `loggingSystem()` — null check e `IllegalStateException`                                                                                                                                                                                                                                                                              |
| B6    | `config/runtime/RuntimeConfigurationService.java`        | `config/runtime/RuntimeConfigurationServiceTest.java` (novo)        | Unit (mocks)                  | `updateLogLevel` happy + exception→`RuntimeConfigurationException`, `setLogLevel` com level null/empty→skip, level inválido→warn, `onRefreshEvent` com exception→error log, `refreshConfiguration`→publica `RefreshEvent`, `getConfigurationSummary` retorna mapa completo                                                                 |
| B7    | `config/runtime/RuntimeConfigurationController.java`     | `config/runtime/RuntimeConfigurationControllerTest.java` (novo)     | @WebMvcTest                   | 5 endpoints com `@PreAuthorize`. **Cenários:** sem auth→403, com scope correto→200, validação de `LogLevelUpdateRequest` (`loggerName` blank, `level` inválido), `getLoggingConfiguration` com `loggingConfig` null no summary (NPE risk L113-115), `validateConfiguration` com result válido e inválido                                   |
| B8    | `config/runtime/RuntimeConfigurationValidator.java`      | `config/runtime/RuntimeConfigurationValidatorTest.java` (novo)      | Unit                          | `validateManually()` com config válida e inválida                                                                                                                                                                                                                                                                                          |
| B9    | `config/shared/LoggingLevelValidator.java`               | `config/shared/LoggingLevelValidatorTest.java` (novo)               | Unit                          | Validação de níveis de log                                                                                                                                                                                                                                                                                                                 |
| B10   | `config/shared/MonitoringConfigValidator.java`           | `config/shared/MonitoringConfigValidatorTest.java` (novo)           | Unit                          | Validação de config de monitoramento                                                                                                                                                                                                                                                                                                       |

#### Grupo C — Properties records com compact constructors

Estes records contêm lógica nos compact constructors (validações, defaults, fallbacks).

| Passo | Arquivo fonte                                    | Arquivo de teste                                            | Tipo | Detalhes                                                          |
| ----- | ------------------------------------------------ | ----------------------------------------------------------- | ---- | ----------------------------------------------------------------- |
| C1    | `config/properties/CorsProperties.java`          | `config/properties/CorsPropertiesTest.java` (novo)          | Unit | Compact constructor com fallback `allowedOrigin`→`allowedOrigins` |
| C2    | `config/properties/ActuatorProperties.java`      | `config/properties/ActuatorPropertiesTest.java` (novo)      | Unit | Lógica no compact constructor                                     |
| C3    | `config/properties/ApplicationProperties.java`   | `config/properties/ApplicationPropertiesTest.java` (novo)   | Unit | Lógica no compact constructor                                     |
| C4    | `config/properties/LocaleProperties.java`        | `config/properties/LocalePropertiesTest.java` (novo)        | Unit | Lógica no compact constructor                                     |
| C5    | `config/properties/LoggingProperties.java`       | `config/properties/LoggingPropertiesTest.java` (novo)       | Unit | Lógica no compact constructor                                     |
| C6    | `config/properties/MessageSourceProperties.java` | `config/properties/MessageSourcePropertiesTest.java` (novo) | Unit | Lógica no compact constructor                                     |
| C7    | `config/properties/MonitoringProperties.java`    | `config/properties/MonitoringPropertiesTest.java` (novo)    | Unit | Lógica no compact constructor                                     |
| C8    | `config/properties/SecurityProperties.java`      | `config/properties/SecurityPropertiesTest.java` (novo)      | Unit | Lógica no compact constructor                                     |

#### Grupo D — Ajustar testes existentes que estão abaixo de 100%

Estes arquivos já têm testes, mas o JaCoCo pode revelar linhas não cobertas. O passo é
idêntico: abrir o relatório, localizar as linhas vermelhas/amarelas, e adicionar os testes faltantes.

| Passo | Arquivo fonte                                                 | Arquivo de teste existente     | Detalhes                                                  |
| ----- | ------------------------------------------------------------- | ------------------------------ | --------------------------------------------------------- |
| D1    | `hydrationtracking/service/WaterIntakeService.java`           | `WaterIntakeServiceTest.java`  | Verificar branches não cobertas no relatório              |
| D2    | `usermanagement/service/UserService.java`                     | `UserServiceTest.java`         | Verificar branches não cobertas no relatório              |
| D3    | `hydrationtracking/mapper/WaterIntakeMapper.java`             | `WaterIntakeMapperTest.java`   | Verificar branches não cobertas no relatório              |
| D4    | `usermanagement/mapper/UserMapper.java`                       | `UserMapperTest.java`          | Verificar branches não cobertas no relatório              |
| D5    | `usermanagement/mapper/PersonalMapper.java`                   | `PersonalMapperTest.java`      | Verificar branches não cobertas no relatório              |
| D6    | `usermanagement/mapper/PhysicalMapper.java`                   | `PhysicalMapperTest.java`      | Verificar branches não cobertas no relatório              |
| D7    | `usermanagement/mapper/AlarmSettingsMapper.java`              | `AlarmSettingsMapperTest.java` | Verificar branches não cobertas no relatório              |
| D8+   | *(qualquer outro arquivo que o JaCoCo mostre abaixo de 100%)* | Teste existente correspondente | Mesma abordagem: analisar → complementar → confirmar 100% |

**Nota:** A lista D é dinâmica. Só será possível determinar os passos exatos após rodar o primeiro
relatório JaCoCo com os excludes ajustados.

**Resultado esperado da Fase 1:** 100% line coverage em **todos** os arquivos não-excluídos
segundo o relatório JaCoCo, confirmado arquivo por arquivo.

---

### 7.2 Fase 2 — Pitest mutation testing (arquivo por arquivo)

**Pré-requisito:** Fase 1 completa (100% line coverage JaCoCo).

**Método por arquivo:**

1. Rodar Pitest para um único arquivo:
   `mvn pitest:mutationCoverage -DtargetClasses=br.com.drinkwater.path.ClassName`
2. Abrir `target/pit-reports/index.html`, localizar o arquivo
3. Identificar mutantes sobreviventes (SURVIVED) — cada um representa uma branch ou condição
   que os testes não detectam
4. Escrever testes que matam os mutantes sobreviventes
5. Re-rodar Pitest para o mesmo arquivo e confirmar 0 mutantes sobreviventes
6. Commit: `test: kill all Pitest mutants for <ClassName>`
7. Passar para o próximo arquivo

**Ordem de execução:** Mesma ordem dos Grupos A→B→C→D da Fase 1, mas agora o foco são mutantes
em vez de linhas não cobertas.

#### Mutantes esperados por classe (estimativa baseada na análise do código)

| Classe                           | Mutantes esperados      | Branches críticas para mutation testing                                                                                               |
| -------------------------------- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `GlobalExceptionHandler`         | Alto (~20-30)           | Switch arms, null checks em `invalidValue`, `requiredType`, `typeMismatch` contains, `lastDotIndex > 0`, status codes de cada handler |
| `SecurityConfig`                 | Médio (~5-10)           | Ordem dos `requestMatchers`, `permitAll` vs `authenticated`, `STATELESS`                                                              |
| `WebhookSecurityFilter`          | Médio (~5-8)            | `equals` no secret, `startsWith` no path, `SC_UNAUTHORIZED`                                                                           |
| `RuntimeConfigurationService`    | Alto (~15-20)           | `null/empty` level check, `LogLevel.valueOf`, try-catch branches, event publishing                                                    |
| `RuntimeConfigurationController` | Médio (~10-15)          | Retornos de status, `result.valid()` branch, map keys                                                                                 |
| `WaterIntakeFilterDTO`           | Médio (~8-12)           | `requireNonNullElse`, `VALID_SORT_FIELDS.contains`, `toUpperCase`, default values                                                     |
| `MessageResolver`                | Baixo (~3-5)            | `requireNonNull` em construtor e retornos                                                                                             |
| Properties records               | Baixo-Médio (~3-8 cada) | Defaults, fallbacks, validações nos compact constructors                                                                              |

**Nota:** Os números exatos só serão conhecidos após rodar o Pitest. A lista é uma estimativa para
dimensionar o esforço.

**Resultado esperado da Fase 2:** 100% mutation score (0 mutantes sobreviventes) em todos os
arquivos não-excluídos, confirmado arquivo por arquivo.

---

### 7.3 Classes excluídas do JaCoCo/Pitest (sem teste necessário)

Estas classes são triviais (pure wiring, records sem lógica, annotations, exceptions simples,
interfaces) e ficam fora do escopo de cobertura:

| Classe                                                     | Motivo da exclusão                                             |
| ---------------------------------------------------------- | -------------------------------------------------------------- |
| `DrinkWaterApiApplication`                                 | Entry point Spring Boot                                        |
| `config/DateTimeConfig`                                    | `@Configuration` com `@Bean` de `ObjectMapper` — pure wiring   |
| `config/LocaleConfig`                                      | `@Configuration` com `@Bean` de `LocaleResolver` — pure wiring |
| `config/MessageSourceConfig`                               | `@Configuration` com `@Bean` de `MessageSource` — pure wiring  |
| `config/ValidationConfig`                                  | `@Configuration` com `@Bean` de `Validator` — pure wiring      |
| `config/WebConfig`                                         | `@Configuration` com `addArgumentResolvers` — pure wiring      |
| `config/health/HealthClientConfig`                         | `@Configuration` com `@Bean` de `RestClient` — pure wiring     |
| `config/KeycloakAdminClientProducer`                       | `@Configuration` com `@Bean` de `Keycloak` — pure wiring       |
| `config/properties/CacheProperties`                        | Record sem compact constructor                                 |
| `config/properties/ContainerProperties`                    | Record sem compact constructor                                 |
| `config/properties/JacksonProperties`                      | Record sem compact constructor                                 |
| `config/properties/ServerProperties`                       | Record sem compact constructor                                 |
| `config/properties/WebhookProperties`                      | Record sem compact constructor                                 |
| `config/runtime/RuntimeConfigurationException`             | Extends `RuntimeException`, sem lógica                         |
| `config/security/AuthenticatedUser`                        | Annotation `@interface`                                        |
| `config/security/InsufficientScopeException`               | Extends `RuntimeException`, campo + getter                     |
| `api/internal/dto/KeycloakEventDTO`                        | Record sem lógica                                              |
| `api/versioning/ApiVersion`                                | Annotation `@interface`                                        |
| `api/versioning/ApiVersionWebConfig`                       | `@Configuration` com `addInterceptors` — pure wiring           |
| `hydrationtracking/dto/WaterIntakeDTO`                     | Record sem lógica                                              |
| `hydrationtracking/dto/WaterIntakeResponseDTO`             | Record sem lógica                                              |
| `hydrationtracking/exception/DuplicateDateTimeException`   | Extends `RuntimeException`, sem lógica                         |
| `hydrationtracking/exception/WaterIntakeNotFoundException` | Extends `RuntimeException`, sem lógica                         |
| `hydrationtracking/repository/WaterIntakeSearchRepository` | Interface sem default methods                                  |
| `hydrationtracking/validation/ValidDateRange`              | Annotation `@interface`                                        |
| `hydrationtracking/validation/ValidVolumeRange`            | Annotation `@interface`                                        |
| `usermanagement/dto/AlarmSettingsDTO`                      | Record sem lógica                                              |
| `usermanagement/dto/AlarmSettingsResponseDTO`              | Record sem lógica                                              |
| `usermanagement/dto/PersonalDTO`                           | Record sem lógica                                              |
| `usermanagement/dto/PhysicalDTO`                           | Record sem lógica                                              |
| `usermanagement/dto/UserResponseDTO`                       | Record sem lógica                                              |
| `usermanagement/exception/UserAlreadyExistsException`      | Extends `RuntimeException`, sem lógica                         |
| `usermanagement/exception/UserNotFoundException`           | Extends `RuntimeException`, sem lógica                         |
| `usermanagement/validation/ValidAlarmTime`                 | Annotation `@interface`                                        |
| `usermanagement/validation/ValidBirthDate`                 | Annotation `@interface`                                        |

---

### 7.4 Estimativa de esforço

| Fase                                      | Arquivos novos | Arquivos ajustados | Esforço estimado |
| ----------------------------------------- | -------------- | ------------------ | ---------------- |
| 7.0 Preparação (JaCoCo + Pitest config)   | 0              | 1 (`pom.xml`)      | ~30 min          |
| 7.1 Fase 1 — Grupo A (core + exception)   | 4              | 0                  | ~3-4h            |
| 7.1 Fase 1 — Grupo B (config com lógica)  | 10             | 0                  | ~6-8h            |
| 7.1 Fase 1 — Grupo C (properties records) | 8              | 0                  | ~2-3h            |
| 7.1 Fase 1 — Grupo D (ajustar existentes) | 0              | ~5-10              | ~2-4h            |
| 7.2 Fase 2 — Pitest mutation kill         | 0              | ~15-22             | ~4-6h            |
| **Total**                                 | **~22**        | **~5-10 + ~15-22** | **~18-26h**      |

**Nota:** O esforço é substancialmente maior que a estimativa original (~4-6h) porque o escopo
agora cobre 100% line coverage + 100% mutation score em **todo** o código com lógica, não apenas
as 5 classes originalmente listadas.

---

## Ciclo 8 — OpenAPI / Swagger [DONE]

**Esforço:** ~6-8h | **Risco:** Baixo | **Arquivos:** ~16-18

A API possui 9 endpoints públicos (2 controllers), 1 endpoint interno (webhook), e 1 controller
de gerenciamento (runtime config) — todos sem nenhuma documentação OpenAPI. O `GlobalExceptionHandler`
produz 15 tipos diferentes de `ProblemDetail` (RFC 7807) que precisam ser mapeados como `@ApiResponse`.
A API usa cursor-based pagination (`CursorPageResponse`) e API versioning via `@ApiVersion` com
headers customizados (`Api-Version`, `Api-Deprecated`, `Sunset`, `Link`).

### 8.0 Preparação — Adicionar dependência e configuração base

**Problema:** `springdoc-openapi` não consta no `pom.xml`. Nenhuma configuração de OpenAPI existe
em `application.yml`. Os paths `/v3/api-docs/**` e `/swagger-ui/**` não estão liberados no
`SecurityConfig`, portanto seriam bloqueados por `.anyRequest().authenticated()`.

**Evidência — `pom.xml` sem dependência springdoc:**

```xml
<!-- pom.xml — nenhuma referência a springdoc-openapi -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- ... 18 dependências, nenhuma de springdoc -->
</dependencies>
```

**Evidência — `SecurityConfig` bloqueia paths de documentação:**

```java
// SecurityConfig.java — filterChain só permite actuator e /internal/**
return http.authorizeHttpRequests(
        authorize ->
                authorize
                        .requestMatchers(actuatorEndpoints).permitAll()
                        .requestMatchers("/internal/**").permitAll()
                        .anyRequest().authenticated())  // <-- bloqueia /swagger-ui/** e /v3/api-docs/**
```

**Ação:**

1. Adicionar `springdoc-openapi-starter-webmvc-ui` ao `pom.xml` (sem version hardcoded — usar
   BOM do Spring Boot se disponível, senão declarar property `<springdoc-openapi.version>`)
2. Configurar bloco `springdoc` em `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: ${SPRINGDOC_ENABLED:true}
  swagger-ui:
    path: /swagger-ui.html
    enabled: ${SPRINGDOC_ENABLED:true}
    operationsSorter: method
    tagsSorter: alpha
    doc-expansion: none
  default-produces-media-type: application/json
  default-consumes-media-type: application/json
```

3. Liberar paths no `SecurityConfig`:

```java
.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
```

4. Adicionar variável `SPRINGDOC_ENABLED=true` ao `.env.example` (permite desabilitar em produção)

**Resultado esperado:** Swagger UI acessível sem autenticação em `/swagger-ui.html`. Spec
OpenAPI 3.0 disponível em `/v3/api-docs` (JSON) e `/v3/api-docs.yaml` (YAML).

---

### 8.1 Configuração global — Info, Security Scheme, Servers

**Problema:** Não existe classe `@Configuration` com `@Bean OpenAPI` para definir metadata da API
(título, versão, descrição, contato) nem o esquema de segurança OAuth2/JWT. Sem isso, o Swagger UI
não exibe o botão "Authorize" e a spec gerada não documenta autenticação.

**Ação:**

1. Criar classe `OpenApiConfig` com `@Bean OpenAPI` contendo:
   - **Info:** título "Drink Water API", versão de `${APP_VERSION}`, descrição do propósito da API
   - **Contact:** nome do projeto, URL do repositório
   - **License:** MIT (já declarada no `pom.xml`)
   - **Security Scheme:** `bearerAuth` do tipo HTTP bearer com formato JWT
   - **Global Security Requirement:** `bearerAuth` aplicado a todos os endpoints por default
   - **Server:** URL base configurável via `${BASE_URL}`

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI drinkWaterOpenAPI(
            @Value("${app.version}") String appVersion,
            @Value("${cors.baseUrl}") String baseUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title("Drink Water API")
                        .version(appVersion)
                        .description("REST API for hydration tracking and user management")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server().url(baseUrl).description("Current environment"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

2. Configurar agrupamento por versão da API para futuras versões:

```yaml
springdoc:
  group-configs:
    - group: v1
      paths-to-match: /api/v1/**
      display-name: "Drink Water API v1"
```

**Resultado esperado:** Spec OpenAPI inclui metadata completa, botão "Authorize" no Swagger UI
permite testar endpoints com JWT, e server URL reflete o ambiente configurado.

---

### 8.2 Fase 1 — Anotar controllers públicos (`@Tag`, `@Operation`, `@ApiResponse`)

**Problema:** Os 2 controllers públicos (`UserControllerV1` com 4 endpoints e
`WaterIntakeControllerV1` com 5 endpoints) não possuem nenhuma anotação OpenAPI. O springdoc
gera documentação automática a partir de `@RequestMapping`, mas sem `@Operation` não há
descriptions, exemplos de request/response, nem mapeamento correto dos códigos de erro.

**Evidência — `UserControllerV1` (4 endpoints, 0 anotações OpenAPI):**

```java
// UserControllerV1.java — sem @Tag, sem @Operation, sem @ApiResponse
@RestController
@RequestMapping("/api/v1/users")
@ApiVersion("v1")
public class UserControllerV1 {

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:read')")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticatedUser UUID publicId) { ... }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:create')")
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO, @AuthenticatedUser UUID publicId) { ... }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:update')")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
            @AuthenticatedUser UUID publicId, @Valid @RequestBody UserDTO updateUserDTO) { ... }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:delete')")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticatedUser UUID publicId) { ... }
}
```

**Evidência — `WaterIntakeControllerV1` (5 endpoints, 0 anotações OpenAPI):**

```java
// WaterIntakeControllerV1.java — sem @Tag, sem @Operation, sem @ApiResponse
@RestController
@RequestMapping("/api/v1/users/water-intakes")
@ApiVersion("v1")
public class WaterIntakeControllerV1 {

    @PostMapping     // create
    @GetMapping("/{id}")  // findById
    @PutMapping("/{id}")  // updateById
    @DeleteMapping("/{id}")  // deleteById
    @GetMapping      // search (retorna CursorPageResponse<WaterIntakeResponseDTO>)
}
```

**Ação:**

Para cada controller, adicionar:
- `@Tag(name = "...", description = "...")` na classe
- `@Operation(summary = "...", description = "...")` em cada método
- `@ApiResponse` para cada código HTTP possível (incluindo erros do `GlobalExceptionHandler`)
- `@Parameter(hidden = true)` em `@AuthenticatedUser UUID publicId` (não é query param, é extraído do JWT)

**Mapeamento de `@ApiResponse` por endpoint:**

| Controller                | Método              | 2xx | Erros possíveis (via `GlobalExceptionHandler`)                 |
| ------------------------- | ------------------- | --- | -------------------------------------------------------------- |
| `UserControllerV1`        | `getCurrentUser`    | 200 | 401, 403, 404 (`UserNotFoundException`)                        |
| `UserControllerV1`        | `createUser`        | 201 | 400 (validation), 401, 403, 409 (`UserAlreadyExistsException`) |
| `UserControllerV1`        | `updateCurrentUser` | 200 | 400 (validation), 401, 403, 404 (`UserNotFoundException`)      |
| `UserControllerV1`        | `deleteCurrentUser` | 204 | 401, 403                                                       |
| `WaterIntakeControllerV1` | `create`            | 201 | 400 (validation, `DuplicateDateTimeException`), 401, 403       |
| `WaterIntakeControllerV1` | `findById`          | 200 | 401, 403, 404 (`WaterIntakeNotFoundException`)                 |
| `WaterIntakeControllerV1` | `updateById`        | 200 | 400 (validation, `DuplicateDateTimeException`), 401, 403, 404  |
| `WaterIntakeControllerV1` | `deleteById`        | 204 | 401, 403                                                       |
| `WaterIntakeControllerV1` | `search`            | 200 | 400 (validation, type-mismatch, missing-param), 401, 403       |

**Resultado esperado:** 9 endpoints públicos documentados com descriptions, exemplos e códigos
de erro completos.

---

### 8.3 Fase 2 — Anotar DTOs com `@Schema`

**Problema:** Os 10 DTOs (5 request + 5 response) são records sem `@Schema`. O springdoc infere
tipos e nomes de campo automaticamente, mas não gera descriptions, exemplos, formatos esperados,
constraints legíveis nem valores permitidos para enums.

**Inventário de DTOs a anotar:**

| DTO                        | Tipo                  | Campos                                                                                           | Anotações existentes (validação)                                                           |
| -------------------------- | --------------------- | ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------ |
| `UserDTO`                  | Request               | `email`, `personal`, `physical`, `settings`                                                      | `@NotBlank`, `@Email`, `@Pattern`, `@Size`, `@NotNull`, `@Valid`                           |
| `PersonalDTO`              | Request (nested)      | `firstName`, `lastName`, `birthDate`, `biologicalSex`                                            | `@NotBlank`, `@Size`, `@Pattern`, `@NotNull`, `@ValidBirthDate`                            |
| `PhysicalDTO`              | Request (nested)      | `weight`, `weightUnit`, `height`, `heightUnit`                                                   | `@NotNull`, `@Positive`, `@DecimalMax`, `@DecimalMin`                                      |
| `AlarmSettingsDTO`         | Request (nested)      | `goal`, `intervalMinutes`, `dailyStartTime`, `dailyEndTime`                                      | `@Min`, `@Max`, `@NotNull`, `@ValidAlarmTime`                                              |
| `WaterIntakeDTO`           | Request               | `dateTimeUTC`, `volume`, `volumeUnit`                                                            | `@NotNull`, `@PastOrPresent`, `@Positive`, `@Max`                                          |
| `WaterIntakeFilterDTO`     | Query params          | `startDate`, `endDate`, `minVolume`, `maxVolume`, `cursor`, `size`, `sortField`, `sortDirection` | `@NotNull`, `@PastOrPresent`, `@Range`, `@Pattern`, `@ValidDateRange`, `@ValidVolumeRange` |
| `UserResponseDTO`          | Response              | `publicId`, `email`, `personal`, `physical`, `settings`                                          | Nenhuma                                                                                    |
| `AlarmSettingsResponseDTO` | Response              | `goal`, `intervalMinutes`, `dailyStartTime`, `dailyEndTime`                                      | Nenhuma                                                                                    |
| `WaterIntakeResponseDTO`   | Response              | `id`, `dateTimeUTC`, `volume`, `volumeUnit`                                                      | Nenhuma                                                                                    |
| `CursorPageResponse<T>`    | Response (pagination) | `content`, `pageSize`, `hasNext`, `nextCursor`                                                   | Nenhuma                                                                                    |

**Enums referenciados nos DTOs (documentar valores permitidos):**

| Enum            | Valores          | Usado em                                   |
| --------------- | ---------------- | ------------------------------------------ |
| `VolumeUnit`    | `ML`             | `WaterIntakeDTO`, `WaterIntakeResponseDTO` |
| `BiologicalSex` | `MALE`, `FEMALE` | `PersonalDTO`                              |
| `WeightUnit`    | `KG`             | `PhysicalDTO`                              |
| `HeightUnit`    | `CM`             | `PhysicalDTO`                              |

**Ação:**

- Adicionar `@Schema(description, example)` nos campos de cada DTO
- Para enums: `@Schema(description = "...", allowableValues = {"ML"})` ou confiar no springdoc
  que infere valores de enums automaticamente
- Para `WaterIntakeFilterDTO`: anotar com `@Parameter` nos campos opcionais para documentar defaults
  (`size` default 10, `sortField` default `dateTimeUTC`, `sortDirection` default `DESC`)
- Para `CursorPageResponse`: anotar para que o schema genérico mostre os tipos concretos

Exemplos de `@Schema`:

```java
// WaterIntakeDTO — antes (sem @Schema)
public record WaterIntakeDTO(
        @NotNull @PastOrPresent Instant dateTimeUTC,
        @Positive @Max(5000) int volume,
        @NotNull VolumeUnit volumeUnit) {}

// WaterIntakeDTO — depois (com @Schema)
public record WaterIntakeDTO(
        @Schema(description = "UTC timestamp of the water intake",
                example = "2025-06-15T14:30:00Z", type = "string", format = "date-time")
        @NotNull @PastOrPresent Instant dateTimeUTC,

        @Schema(description = "Volume consumed in the specified unit",
                example = "250", minimum = "1", maximum = "5000")
        @Positive @Max(5000) int volume,

        @Schema(description = "Unit of measurement for the volume",
                example = "ML")
        @NotNull VolumeUnit volumeUnit) {}
```

**Resultado esperado:** Spec OpenAPI gera schemas com descriptions, exemplos e constraints para
todos os DTOs. Swagger UI mostra exemplos preenchidos nos formulários de teste.

---

### 8.4 Fase 3 — Documentar error responses (ProblemDetail / RFC 7807)

**Problema:** O `GlobalExceptionHandler` produz 15 tipos de erro usando `ProblemDetail` (RFC 7807),
mas nenhum deles aparece na spec OpenAPI. Clientes não sabem a estrutura de erros sem inspecionar
o código.

**Evidência — `GlobalExceptionHandler` produz ProblemDetail com type URIs customizadas:**

```java
// Todos os erros seguem o padrão ProblemDetail com:
// - type: https://www.drinkwater.com.br/{typeSlug}
// - status: HTTP status code
// - detail: mensagem localizada
// Erros de validação adicionam property "errors" com lista de {field, message}
```

**Erros documentáveis (15 tipos):**

| Type Slug                     | Status | Quando ocorre                                               |
| ----------------------------- | ------ | ----------------------------------------------------------- |
| `invalid-argument`            | 400    | `IllegalArgumentException`                                  |
| `insufficient-scope`          | 403    | Scope OAuth2 insuficiente (+ `required_scope`)              |
| `forbidden`                   | 403    | Acesso negado genérico                                      |
| `waterintake-not-found`       | 404    | `WaterIntakeNotFoundException`                              |
| `time-range-validation-error` | 400    | `DuplicateDateTimeException`                                |
| `user-not-found`              | 404    | `UserNotFoundException`                                     |
| `user-already-exists`         | 409    | `UserAlreadyExistsException`                                |
| `runtime-configuration-error` | 500    | `RuntimeConfigurationException`                             |
| `internal-server-error`       | 500    | Exceções não tratadas                                       |
| `parsing-error`               | 400    | JSON mal formado (+ `errors` para `InvalidFormatException`) |
| `validation-error`            | 400    | Falha de validação de `@Valid` (+ lista de `errors`)        |
| `constraint-violation`        | 400    | `ConstraintViolationException` (+ lista de `errors`)        |
| `type-mismatch`               | 400    | Tipo de parâmetro incorreto (+ `errors`)                    |
| `missing-parameter`           | 400    | Parâmetro obrigatório ausente (+ `errors`)                  |

**Ação:**

1. Criar schema customizado para `ProblemDetail` com `@Schema` para que a spec documente:
   - `type` (URI), `title`, `status`, `detail`, `instance`
   - Properties opcionais: `errors` (lista de `{field, message}`), `required_scope` (string)

2. Anotar os `@ApiResponse` nos controllers referenciando o schema de `ProblemDetail`:

```java
@ApiResponse(responseCode = "400", description = "Validation error",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
```

3. Considerar criar constantes ou anotações compostas para evitar repetição dos `@ApiResponse`
   mais comuns (400, 401, 403, 500).

**Resultado esperado:** Spec OpenAPI documenta todos os cenários de erro com estrutura
`ProblemDetail`, incluindo os campos extras (`errors`, `required_scope`).

---

### 8.5 Fase 4 — Esconder endpoints internos e de gerenciamento

**Problema:** O springdoc expõe por default **todos** os controllers, incluindo:
- `KeycloakWebhookController` (`/internal/webhooks/keycloak`) — endpoint interno protegido por
  webhook secret, não destinado a clientes da API
- `RuntimeConfigurationController` (`/management/runtime-config/**`) — 5 endpoints admin que não
  fazem parte do contrato público da API

Incluí-los na spec pública gera confusão e expõe superfície interna desnecessariamente.

**Evidência — Controllers que devem ser ocultos:**

```java
// KeycloakWebhookController — interno, sem JWT, usa webhook secret
@RestController
@RequestMapping("/internal/webhooks")
public class KeycloakWebhookController { ... }

// RuntimeConfigurationController — admin, requer ROLE_ADMIN ou scope admin
@RestController
@RequestMapping("/management/runtime-config")
public class RuntimeConfigurationController { ... }
```

**Ação:**

Opção A — Configurar via `application.yml` (preferível, sem alterar código):

```yaml
springdoc:
  paths-to-match: /api/**
```

Opção B — Adicionar `@Hidden` nos controllers internos:

```java
@Hidden  // io.swagger.v3.oas.annotations.Hidden
@RestController
@RequestMapping("/internal/webhooks")
public class KeycloakWebhookController { ... }
```

A **Opção A** é preferível porque filtra por convention (tudo em `/api/**` é público) sem
acoplar controllers internos à dependência do springdoc.

**Resultado esperado:** Swagger UI e spec OpenAPI exibem apenas os 9 endpoints públicos em
`/api/v1/**`. Endpoints internos e de gerenciamento ficam ocultos.

---

### 8.6 Fase 5 — Resolver `@AuthenticatedUser` no springdoc

**Problema:** O parâmetro `@AuthenticatedUser UUID publicId` é uma anotação customizada que extrai
o `sub` claim do JWT. O springdoc não reconhece anotações customizadas de resolução de argumento
e as interpreta como query parameter, gerando um campo `publicId` nos formulários do Swagger UI.

**Evidência — `@AuthenticatedUser` é resolvido via `HandlerMethodArgumentResolver`:**

```java
// Todos os endpoints públicos usam:
public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticatedUser UUID publicId) { ... }
// O springdoc vai gerar um parâmetro "publicId" do tipo query — incorreto
```

**Ação:**

Opção A — Adicionar `@Parameter(hidden = true)` em cada ocorrência (9 endpoints):

```java
public ResponseEntity<UserResponseDTO> getCurrentUser(
        @Parameter(hidden = true) @AuthenticatedUser UUID publicId) { ... }
```

Opção B — Configurar `ParameterCustomizer` global (melhor, DRY):

```java
@Bean
public ParameterCustomizer authenticatedUserParameterCustomizer() {
    return (parameterModel, methodParameter) -> {
        if (methodParameter.hasParameterAnnotation(AuthenticatedUser.class)) {
            return null;  // remove o parâmetro da spec
        }
        return parameterModel;
    };
}
```

A **Opção B** é preferível para não poluir todos os controllers com anotações do springdoc,
mantendo o código de domínio desacoplado da dependência de documentação.

**Resultado esperado:** O campo `publicId` não aparece como parâmetro no Swagger UI. A
autenticação é representada apenas pelo security scheme `bearerAuth`.

---

### 8.7 Estimativa de esforço

| Fase                                                            | Arquivos novos | Arquivos ajustados                                 | Esforço estimado |
| --------------------------------------------------------------- | -------------- | -------------------------------------------------- | ---------------- |
| 8.0 Preparação (dependência + config + security)                | 0              | 3 (`pom.xml`, `application.yml`, `SecurityConfig`) | ~30 min          |
| 8.1 Config global (OpenApiConfig bean)                          | 1              | 0                                                  | ~30 min          |
| 8.2 Fase 1 — Controllers (`@Tag`, `@Operation`, `@ApiResponse`) | 0              | 2 (`UserControllerV1`, `WaterIntakeControllerV1`)  | ~2h              |
| 8.3 Fase 2 — DTOs (`@Schema`)                                   | 0              | ~10 (DTOs + enums + `CursorPageResponse`)          | ~2h              |
| 8.4 Fase 3 — Error responses (ProblemDetail schema)             | 0-1            | 0                                                  | ~30 min          |
| 8.5 Fase 4 — Esconder endpoints internos                        | 0              | 1 (`application.yml`)                              | ~15 min          |
| 8.6 Fase 5 — Resolver `@AuthenticatedUser`                      | 0-1            | 0                                                  | ~15 min          |
| **Total**                                                       | **1-3**        | **~16**                                            | **~6-8h**        |

**Nota:** O esforço é maior que a estimativa original (~3-4h) porque a API possui mais endpoints (9)
e mais DTOs (10) do que o inicialmente previsto, além de exigir tratamento específico para o
`@AuthenticatedUser` customizado, os 15 tipos de `ProblemDetail`, e a separação entre endpoints
públicos e internos.

---

## Ciclo 9 — Resiliência na integração com Keycloak [DONE]

**Esforço:** ~6-8h | **Risco:** Médio (nova dependência, design de fallback, testes de resiliência) | **Arquivos:** 8-12

### 9.0 Diagnóstico — estado atual da integração com Keycloak

Antes de adicionar resiliência, é preciso entender o que existe hoje:

**Bean `Keycloak` produzido mas não consumido:**

```java
// KeycloakAdminClientProducer.java — bean criado, mas nenhum service o injeta
@Configuration
@Profile("!it-no-containers")
public class KeycloakAdminClientProducer {
    @Bean
    Keycloak configKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.url())
                .realm(keycloakProperties.realm())
                .clientId(keycloakProperties.clientId())
                .username(keycloakProperties.username())
                .password(keycloakProperties.password())
                .build();
    }
}
```

O `UserService` não usa o Keycloak Admin Client — trabalha apenas com o `publicId` (sub claim do
JWT) e o banco local. O `KeycloakWebhookController` recebe eventos _do_ Keycloak (Keycloak → API),
mas não faz chamadas _para_ o Keycloak.

**Pontos que realmente chamam o Keycloak externamente:**

| Componente                       | Chamada                                                      | Timeout atual           | Resiliência |
| -------------------------------- | ------------------------------------------------------------ | ----------------------- | ----------- |
| `KeycloakHealthIndicator`        | GET `/.well-known/openid-configuration` via JDK HttpClient   | 5s connect + 5s request | Nenhuma     |
| JWT validation (Spring Security) | JWK Set fetch via `jwkSetUri` configurado no resource server | Padrão do Nimbus (~5s)  | Nenhuma     |
| `KeycloakAdminClientProducer`    | Bean criado (lazy token fetch no primeiro uso)               | Nenhum configurado      | Nenhuma     |

**Decisão:** O ciclo 9 deve focar em três frentes:
1. Tornar o health check do Keycloak resiliente (retry + circuit breaker)
2. Preparar o `Keycloak` bean com timeout adequado para uso futuro
3. Criar infraestrutura de resiliência reutilizável para qualquer integração externa

---

### 9.1 Fase 1 — Dependência e configuração base do Resilience4j

**Problema:** Não existe nenhuma dependência ou configuração de resiliência no projeto. O
`spring-cloud-context` já está no `pom.xml`, mas não há módulos de circuit breaker.

**Evidência — `pom.xml` sem Resilience4j:**

```xml
<!-- Presente -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-context</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Ausente: nenhum módulo de resiliência -->
```

**Ação:**

1. Adicionar `resilience4j-spring-boot3` e `spring-boot-starter-aop` ao `pom.xml`:

```xml
<properties>
    <resilience4j.version>2.3.0</resilience4j.version>
</properties>

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>${resilience4j.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

A dependência `spring-boot-starter-aop` é obrigatória para que as anotações `@CircuitBreaker`,
`@Retry` e `@TimeLimiter` funcionem via proxy AOP. Já existe `spring-boot-starter-actuator`, que
expõe automaticamente os endpoints `/actuator/circuitbreakers`, `/actuator/retries` etc.

2. Configurar instâncias nomeadas em `application.yml`:

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        register-health-indicator: true
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        record-exceptions:
          - java.io.IOException
          - java.net.ConnectException
          - java.net.http.HttpTimeoutException
          - jakarta.ws.rs.ProcessingException
        ignore-exceptions:
          - java.lang.IllegalArgumentException
    instances:
      keycloak:
        base-config: default
        sliding-window-size: 5
        wait-duration-in-open-state: 60s

  retry:
    configs:
      default:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2.0
        retry-exceptions:
          - java.io.IOException
          - java.net.ConnectException
          - java.net.http.HttpTimeoutException
          - jakarta.ws.rs.ProcessingException
        ignore-exceptions:
          - java.lang.IllegalArgumentException
    instances:
      keycloak:
        base-config: default
        max-attempts: 2
        wait-duration: 500ms

  timelimiter:
    configs:
      default:
        timeout-duration: 5s
        cancel-running-future: true
    instances:
      keycloak:
        base-config: default
        timeout-duration: 3s
```

3. Expor métricas do Resilience4j no Actuator/Prometheus:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,circuitbreakers,retries
  health:
    circuitbreakers:
      enabled: true
```

**Resultado esperado:** Resilience4j configurado com instância `keycloak` de circuit breaker,
retry e time limiter. Métricas expostas via Actuator para observabilidade no Grafana (ciclo 4).

---

### 9.2 Fase 2 — Resiliência no `KeycloakHealthIndicator`

**Problema:** O `KeycloakHealthIndicator` faz chamada HTTP síncrona sem retry. Se o Keycloak
retornar timeout uma única vez, o health check reporta `DOWN` imediatamente, mesmo que seja
uma falha transiente. Isso pode causar restart desnecessário em ambientes com liveness probe.

**Evidência — chamada sem resiliência:**

```java
// KeycloakHealthIndicator.java — sem retry, falha única = DOWN
@Override
public Health health() {
    try {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(wellKnownUrl))
                .timeout(TIMEOUT)  // 5s, sem retry
                .GET()
                .build();
        HttpResponse<Void> response =
                httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() == 200) {
            return Health.up().withDetail("url", wellKnownUrl).build();
        }
        return Health.down().withDetail("status", response.statusCode()).build();
    } catch (Exception e) {
        return Health.down().withException(e).build();
    }
}
```

**Ação:**

Opção A — Usar anotações `@Retry` + `@CircuitBreaker` do Resilience4j (requer refactoring):

```java
@Component
@Profile("!it-no-containers")
public class KeycloakHealthIndicator implements HealthIndicator {

    private final KeycloakHealthClient keycloakHealthClient;

    @Override
    public Health health() {
        try {
            int statusCode = keycloakHealthClient.checkKeycloak();
            if (statusCode == 200) {
                return Health.up().withDetail("url", keycloakHealthClient.getWellKnownUrl()).build();
            }
            return Health.down().withDetail("status", statusCode).build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}

@Component
@Profile("!it-no-containers")
public class KeycloakHealthClient {

    private final String wellKnownUrl;
    private final HttpClient httpClient;

    @Retry(name = "keycloak")
    @CircuitBreaker(name = "keycloak", fallbackMethod = "healthFallback")
    public int checkKeycloak() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(wellKnownUrl))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int healthFallback(Exception e) {
        log.warn("Keycloak health check fallback triggered: {}", e.getMessage());
        return -1;  // sinaliza fallback para o HealthIndicator tratar como DOWN
    }
}
```

Opção B — Usar Resilience4j programático sem anotações (menos acoplamento):

```java
@Component
@Profile("!it-no-containers")
public class KeycloakHealthIndicator implements HealthIndicator {

    private final HttpClient httpClient;
    private final String wellKnownUrl;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public KeycloakHealthIndicator(
            KeycloakProperties props, HttpClient httpClient,
            RetryRegistry retryRegistry, CircuitBreakerRegistry cbRegistry) {
        this.wellKnownUrl = props.url() + "/realms/" + props.realm()
                + "/.well-known/openid-configuration";
        this.httpClient = httpClient;
        this.retry = retryRegistry.retry("keycloak");
        this.circuitBreaker = cbRegistry.circuitBreaker("keycloak");
    }

    @Override
    public Health health() {
        try {
            var decorated = CircuitBreaker.decorateCheckedSupplier(circuitBreaker,
                    Retry.decorateCheckedSupplier(retry, this::doHealthCheck));
            int statusCode = decorated.get();
            // ...
        } catch (Throwable e) { ... }
    }
}
```

A **Opção A** é preferível porque mantém a separação de responsabilidades (client isolado,
health indicator orquestra), facilita testes unitários com mock do client, e segue o padrão
declarativo do Spring Boot. As anotações do Resilience4j requerem que o método decorado esteja
em um bean Spring separado (limitação do proxy AOP — self-invocation não funciona).

**Resultado esperado:** Health check do Keycloak tolera falhas transientes (até 2 retries com
backoff exponencial). Circuit breaker abre após 3 falhas em 5 chamadas, evitando flood de
requests para um Keycloak indisponível. Fallback retorna status claro.

---

### 9.3 Fase 3 — Configurar timeout no Keycloak Admin Client bean

**Problema:** O `KeycloakBuilder` não configura timeout para conexão nem para requisição. No
primeiro uso (lazy), o Admin Client faz token fetch para
`/realms/{realm}/protocol/openid-connect/token`. Sem timeout, a thread pode bloquear
indefinidamente se o Keycloak estiver atrás de proxy ou firewall com problemas.

**Evidência — builder sem timeout:**

```java
// KeycloakAdminClientProducer.java — sem timeout configurado
return KeycloakBuilder.builder()
        .serverUrl(keycloakProperties.url())
        .realm(keycloakProperties.realm())
        .clientId(keycloakProperties.clientId())
        .username(keycloakProperties.username())
        .password(keycloakProperties.password())
        .build();  // usa defaults do Apache HttpClient interno — sem timeout explícito
```

**Ação:**

Configurar o `ResteasyClient` com timeouts explícitos:

```java
@Bean
Keycloak configKeycloak() {
    return KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.url())
            .realm(keycloakProperties.realm())
            .clientId(keycloakProperties.clientId())
            .username(keycloakProperties.username())
            .password(keycloakProperties.password())
            .resteasyClient(
                    new ResteasyClientBuilderImpl()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .connectionPoolSize(5)
                            .build())
            .build();
}
```

Extrair os valores de timeout para `application.yml` via properties:

```yaml
keycloak:
  admin-client:
    connect-timeout: 5s
    read-timeout: 10s
    connection-pool-size: 5
```

Atualizar o `KeycloakProperties` record para incluir os novos campos com defaults:

```java
@ConfigurationProperties(prefix = "keycloak")
@Validated
public record KeycloakProperties(
        // ... campos existentes ...
        @DefaultValue("5s") Duration adminClientConnectTimeout,
        @DefaultValue("10s") Duration adminClientReadTimeout,
        @DefaultValue("5") int adminClientConnectionPoolSize) {
    // ...
}
```

**Nota:** Embora o bean `Keycloak` não seja consumido atualmente por nenhum service, configurar
timeout é preventivo e essencial para quando for utilizado (ex: sync de roles, deleção de usuário
no Keycloak, etc.). É uma boa prática configurar timeouts no momento da criação do bean, não
depois que um problema em produção ocorrer.

**Resultado esperado:** Admin Client com timeout explícito de 5s (connect) e 10s (read). Threads
nunca bloqueiam indefinidamente por chamadas ao Keycloak.

---

### 9.4 Fase 4 — Service wrapper resiliente para o Keycloak Admin Client

**Problema:** Quando o `Keycloak` bean passar a ser consumido (ex: para deletar usuário no
Keycloak quando o `UserService.deleteUser()` é chamado), as chamadas precisam ter circuit breaker
e retry. Anotações do Resilience4j não funcionam em chamadas diretas ao `Keycloak` (bean
third-party) — é necessário um wrapper.

**Ação:**

Criar `KeycloakAdminService` como camada resiliente entre o domínio e o Admin Client:

```java
@Service
@Profile("!it-no-containers")
public class KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    @Retry(name = "keycloak")
    @CircuitBreaker(name = "keycloak", fallbackMethod = "deleteUserFallback")
    public void deleteUser(UUID publicId) {
        keycloak.realm(keycloakProperties.realm())
                .users()
                .delete(publicId.toString());
        log.info("User {} deleted from Keycloak", publicId);
    }

    private void deleteUserFallback(UUID publicId, Exception e) {
        log.error("Failed to delete user {} from Keycloak after retries: {}",
                publicId, e.getMessage());
        throw new KeycloakOperationException(
                "Keycloak is unavailable. User deleted locally but not in identity provider.", e);
    }
}
```

Criar exceção específica para falhas do Keycloak:

```java
public class KeycloakOperationException extends RuntimeException {
    public KeycloakOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Registrar handler no `GlobalExceptionHandler`:

```java
@ExceptionHandler(KeycloakOperationException.class)
public ResponseEntity<Object> handleKeycloakOperationException(
        KeycloakOperationException ex, WebRequest request) {
    return buildResponse(ex, request, HttpStatus.SERVICE_UNAVAILABLE,
            "exception.keycloak.unavailable", "keycloak-unavailable");
}
```

**Alternativa para produção:** Se a deleção no Keycloak falhar, considerar um mecanismo de
compensação assíncrono (outbox pattern ou scheduled retry) para garantir eventual consistency.
Isso fica fora do escopo deste ciclo, mas deve ser considerado para produção.

**Resultado esperado:** Camada `KeycloakAdminService` encapsula todas as chamadas ao Admin Client
com circuit breaker + retry. O domínio (`UserService`) não lida diretamente com resiliência.

---

### 9.5 Fase 5 — Testes de resiliência

**Problema:** Mecanismos de resiliência precisam de testes que validem o comportamento sob falha.
Sem testes, não há garantia de que o circuit breaker abre, o retry executa, ou o fallback retorna
a resposta esperada.

**Ação:**

1. **Testes unitários do `KeycloakHealthClient`:**

```java
@ExtendWith(MockitoExtension.class)
class KeycloakHealthClientTest {

    // Testar que retry é acionado N vezes antes de fallback
    // Testar que circuit breaker abre após threshold de falhas
    // Testar que fallback retorna valor esperado
    // Testar que exceções ignoradas não acionam retry/circuit breaker
}
```

2. **Testes unitários do `KeycloakAdminService`:**

```java
@ExtendWith(MockitoExtension.class)
class KeycloakAdminServiceTest {

    // Testar retry em ProcessingException (Keycloak unavailable)
    // Testar fallback lança KeycloakOperationException
    // Testar chamada bem-sucedida sem retry
}
```

3. **Teste de integração com Resilience4j real** (usando `@SpringBootTest` slice):

```java
@SpringBootTest
@ActiveProfiles("it-no-containers")
class KeycloakResilienceIT {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void circuitBreakerShouldOpenAfterFailures() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("keycloak");
        // Simular falhas, verificar que estado muda para OPEN
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
```

4. **Teste do `GlobalExceptionHandler` para `KeycloakOperationException`:**

```java
@Test
void shouldReturn503WhenKeycloakUnavailable() {
    // Verificar que retorna 503 SERVICE_UNAVAILABLE com ProblemDetail
}
```

**Resultado esperado:** Cobertura de testes para todos os cenários de resiliência: retry
bem-sucedido, circuit breaker aberto, fallback executado, timeout, e exceções ignoradas.

---

### 9.6 Fase 6 — Observabilidade dos padrões de resiliência

**Problema:** Circuit breaker e retry são invisíveis sem métricas. Em produção, é necessário
saber quando o circuit breaker abre, quantos retries estão ocorrendo, e a taxa de sucesso/falha.

**Evidência — métricas não disponíveis:**

O Actuator já está configurado (ciclo 4), e o Resilience4j 2.3.0 exporta métricas Micrometer
automaticamente. Porém, sem dashboards, as métricas ficam inexploradas.

**Ação:**

1. Verificar que as métricas do Resilience4j aparecem no `/actuator/prometheus`:

```
# Métricas esperadas após configuração:
resilience4j_circuitbreaker_state{name="keycloak"} 0  # 0=CLOSED, 1=OPEN, 2=HALF_OPEN
resilience4j_circuitbreaker_calls_seconds_count{name="keycloak",kind="successful"}
resilience4j_circuitbreaker_calls_seconds_count{name="keycloak",kind="failed"}
resilience4j_circuitbreaker_failure_rate{name="keycloak"}
resilience4j_retry_calls_total{name="keycloak",kind="successful_without_retry"}
resilience4j_retry_calls_total{name="keycloak",kind="successful_with_retry"}
resilience4j_retry_calls_total{name="keycloak",kind="failed_with_retry"}
```

2. Criar dashboard Grafana (se ciclo 4 já implementado) ou documentar queries PromQL:

```promql
# Taxa de falha do circuit breaker (últimos 5 min)
resilience4j_circuitbreaker_failure_rate{name="keycloak"}

# Retries por segundo
rate(resilience4j_retry_calls_total{name="keycloak"}[5m])

# Tempo no estado OPEN
resilience4j_circuitbreaker_state{name="keycloak"} == 1
```

3. Configurar logging de eventos do circuit breaker:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      keycloak:
        event-consumer-buffer-size: 20
```

```java
@PostConstruct
public void registerCircuitBreakerEvents() {
    circuitBreakerRegistry.circuitBreaker("keycloak")
            .getEventPublisher()
            .onStateTransition(event ->
                    log.warn("Keycloak circuit breaker: {}", event));
}
```

**Resultado esperado:** Métricas de resiliência visíveis no Prometheus/Grafana. Transições
de estado do circuit breaker logadas em WARN para alertas.

---

### 9.7 Estimativa de esforço

| Fase                                         | Arquivos novos | Arquivos ajustados                                         | Esforço estimado |
| -------------------------------------------- | -------------- | ---------------------------------------------------------- | ---------------- |
| 9.1 Dependência + config base                | 0              | 2 (`pom.xml`, `application.yml`)                           | ~30 min          |
| 9.2 Resiliência no `KeycloakHealthIndicator` | 1              | 1 (`KeycloakHealthIndicator`, novo `KeycloakHealthClient`) | ~1.5h            |
| 9.3 Timeout no Admin Client bean             | 0              | 2 (`KeycloakAdminClientProducer`, `KeycloakProperties`)    | ~30 min          |
| 9.4 Service wrapper resiliente               | 2              | 1 (`GlobalExceptionHandler`)                               | ~1.5h            |
| 9.5 Testes de resiliência                    | 3-4            | 0                                                          | ~2h              |
| 9.6 Observabilidade                          | 0-1            | 1 (`application.yml`)                                      | ~30 min          |
| **Total**                                    | **6-8**        | **~7**                                                     | **~6-8h**        |

**Nota sobre a ordem de decoração do Resilience4j:** A ordem recomendada é
Retry → CircuitBreaker → TimeLimiter (de dentro para fora). Quando usar anotações, o
Resilience4j Spring Boot aplica na ordem: Retry(CircuitBreaker(TimeLimiter(method))). Isso
garante que o retry reexecuta a chamada, o circuit breaker conta falhas após retries esgotados,
e o time limiter protege contra chamadas infinitas. A configuração em `application.yml` com
`resilience4j.circuitbreaker.instances.keycloak` automaticamente aplica essa ordem.

**Pré-requisitos:**
- Ciclo 4 (observabilidade) é recomendado mas não obrigatório — as métricas do Resilience4j
  funcionam standalone via Actuator
- `spring-boot-starter-actuator` já está no `pom.xml`
- `spring-cloud-context` já está no `pom.xml` (necessário para refresh de configuração)

---

## Resumo

| Ciclo     | Escopo                                                                     | Esforço     | Arquivos        |
| --------- | -------------------------------------------------------------------------- | ----------- | --------------- |
| 1         | Correções pontuais (rename, null check, doc SpotBugs)                      | ~15 min     | 2-3             |
| 2         | Docker Compose local funcional                                             | ~30 min     | 1-2             |
| 3         | Logging no domínio                                                         | ~1h         | 4-6             |
| 4         | Observabilidade (Prometheus + Loki + Grafana)                              | ~3-4h       | 5-8             |
| 5         | Cache local com Caffeine (resolução de userId)                             | ~2h         | 4-5             |
| 6         | Javadoc                                                                    | ~4-5h       | ~35             |
| 7         | Cobertura de testes (JaCoCo 100%; Pitest adiado — incompatível com JDK 25) | ~13-19h     | ~22 novos + pom |
| 8         | OpenAPI / Swagger                                                          | ~6-8h       | ~16-18          |
| 9         | Resiliência Keycloak (Resilience4j circuit breaker + retry + timeout)      | ~6-8h       | ~13-15          |
| **Total** |                                                                            | **~41-55h** |                 |
