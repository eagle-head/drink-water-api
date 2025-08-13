# Claude Agent System

## Overview

This directory contains the configuration and implementation of specialized sub-agents for the Drink Water API project.
The system enables modular, task-specific automation through orchestrated agents.

## Directory Structure

```
.claude/
├── agents/          # Specialized sub-agents
├── commands/        # Orchestration commands
├── templates/       # Agent templates and examples
└── README.md        # This file
```

## Directories

### 📁 agents/

**Purpose:** Contains specialized sub-agents for specific tasks

**Usage:**

- Each agent is a self-contained module with a specific responsibility
- Agents can be triggered manually or automatically via hooks
- Configuration files define agent behavior and parameters

**Available Agents:**

**Planners (Architects):**
- `security-strategy-architect` - Security architecture analysis
- `devops-infrastructure-planner` - Infrastructure and CI/CD planning
- `performance-strategy-analyst` - Performance bottleneck analysis
- `database-migration-architect` - Migration strategy design
- `api-design-strategist` - API governance and documentation strategy

**Executors (Implementers):**
- `security-implementation-specialist` - Security implementation
- `devops-pipeline-executor` - Pipeline and deployment execution
- `jpa-performance-executor` - Database optimization implementation
- `database-migration-executor` - Migration script execution
- `api-documentation-generator` - Documentation generation
- `test-implementation-specialist` - Test suite implementation
- `container-optimization-executor` - Container optimization
- `monitoring-implementation-specialist` - Observability implementation

**Creating New Agents:**

1. Use templates from `templates/` as starting point
2. Define agent configuration (YAML/JSON)
3. Specify triggers and integration points
4. Test agent in isolation before integration

### 📁 commands/

**Purpose:** Orchestration commands for coordinating multiple agents

**Usage:**

- Define workflows that combine multiple agents
- Create command chains for complex tasks
- Manage agent dependencies and execution order

**Command Types:**

- `review-and-test` - Run code review then generate tests
- `document-api` - Extract and document all API endpoints
- `full-analysis` - Complete codebase analysis workflow
- `pre-commit` - Pre-commit validation pipeline

**Command Structure:**

```yaml
name: command-name
agents:
  - agent1
  - agent2
sequence: parallel|sequential
conditions:
  - success
  - failure
```

### 📁 templates/

**Purpose:** Template agents demonstrating best practices

**Available Templates:**

1. **code-reviewer.md** - Code review automation template
2. **test-generator.md** - Test generation template
3. **api-documenter.md** - API documentation template
4. **refactoring-assistant.md** - Refactoring suggestions template

**Using Templates:**

1. Copy template to `agents/` directory
2. Customize configuration for your needs
3. Adjust prompts and parameters
4. Test with sample inputs

## Quick Start

### 1. Create Your First Agent

```bash
cp templates/code-reviewer.md agents/my-reviewer.agent
# Edit configuration and prompts
```

### 2. Define an Orchestration Command

```bash
cat > commands/review-pipeline.cmd << EOF
name: review-pipeline
agents:
  - my-reviewer
  - test-generator
sequence: sequential
EOF
```

### 3. How Claude Code Automatically Selects Agents

Claude Code intelligently selects the appropriate agent(s) based on your natural language prompt:

**Example Prompts:**

```
"Analyze our OAuth2 security and implement rate limiting"
→ security-strategy-architect → security-implementation-specialist

"Our app is slow, optimize database queries and add caching"  
→ performance-strategy-analyst → jpa-performance-executor

"Set up CI/CD pipeline for this Spring Boot project"
→ devops-infrastructure-planner → devops-pipeline-executor

"Create comprehensive API documentation with OpenAPI specs"
→ api-design-strategist → api-documentation-generator

"Write tests for the UserService class"
→ test-implementation-specialist

"Our Docker images are too large, optimize them"
→ container-optimization-executor
```

**Automatic Selection Logic:**
- **Keywords**: Matches prompt keywords to agent specializations
- **Task Type**: Analysis tasks → PLANNERS, Implementation tasks → EXECUTORS
- **Complexity**: Complex requests automatically chain PLANNER → EXECUTOR
- **Context**: Considers Spring Boot, OAuth2, PostgreSQL project context

See `AGENT_SELECTION_GUIDE.md` for detailed selection algorithms.

## Model Selection Strategy

### Model Guidelines
- **Haiku**: Fast, simple tasks (code formatting, basic validation)
- **Sonnet**: Balanced tasks (test generation, documentation, standard code review)
- **Opus**: Complex, critical tasks (security analysis, performance optimization, database migrations)

### Agent Architecture: Planner vs Executor

**🧠 PLANNERS (Opus - Analysis & Strategy):**
- `security-strategy-architect` → **Opus** (OAuth2/Keycloak analysis, security architecture)
- `devops-infrastructure-planner` → **Opus** (CI/CD pipeline design, infrastructure planning)
- `performance-strategy-analyst` → **Opus** (Performance bottleneck analysis, scalability planning)
- `database-migration-architect` → **Opus** (Migration strategy planning, zero-downtime design)
- `api-design-strategist` → **Opus** (API governance, documentation strategy)

**🔧 EXECUTORS (Sonnet/Haiku - Implementation):**
- `security-implementation-specialist` → **Sonnet** (OAuth2 implementation, rate limiting)
- `devops-pipeline-executor` → **Sonnet** (GitHub Actions, container deployment)
- `jpa-performance-executor` → **Sonnet** (Query optimization, caching implementation)
- `database-migration-executor` → **Sonnet** (Flyway scripts, rollback procedures)
- `api-documentation-generator` → **Haiku** (OpenAPI specs, Postman collections)
- `test-implementation-specialist` → **Sonnet** (Unit tests, integration tests, mutation testing)
- `container-optimization-executor` → **Haiku** (Docker optimization, multi-stage builds)
- `monitoring-implementation-specialist` → **Sonnet** (Metrics, tracing, alerting)

## Best Practices

### Agent Design

- **Single Responsibility:** Each agent should do one thing well
- **Clear Interfaces:** Define clear input/output contracts
- **Error Handling:** Include robust error handling
- **Logging:** Implement comprehensive logging
- **Testing:** Create test cases for each agent

### Orchestration

- **Dependency Management:** Clearly define agent dependencies
- **Failure Handling:** Plan for agent failures
- **Performance:** Consider parallel vs sequential execution
- **Monitoring:** Track agent execution metrics

### Naming Conventions

- Agents: `{purpose}-{type}.agent` (e.g., `api-reviewer.agent`)
- Commands: `{workflow}-{action}.cmd` (e.g., `release-validation.cmd`)
- Templates: `{purpose}-{type}.md` (e.g., `security-scanner.md`)

## Integration Points

### Git Hooks

```bash
# .git/hooks/pre-commit
claude run command:pre-commit
```

### CI/CD Pipeline

```yaml
# .github/workflows/main.yml
- name: Run Claude Agents
  run: claude run command:ci-validation
```

### IDE Integration

- VS Code: Install Claude extension
- IntelliJ: Configure external tools
- Command line: Use `claude` CLI

## Advanced Features

### Agent Composition

Combine multiple agents for complex workflows:

```yaml
super-agent:
  compose:
    - basic-reviewer
    - security-scanner
    - performance-analyzer
```

### Conditional Execution

Execute agents based on conditions:

```yaml
conditions:
  - files_changed: "*.java"
  - branch: "main|develop"
  - pr_size: "<500"
```

### Custom Triggers

Define custom triggers for agents:

```yaml
triggers:
  - file_save: "*.java"
  - pr_open: true
  - schedule: "0 0 * * *"
```

## Troubleshooting

### Common Issues

1. **Agent not found:** Check agent file exists in `agents/`
2. **Command fails:** Verify all referenced agents exist
3. **Timeout:** Increase timeout in agent configuration
4. **Memory issues:** Reduce parallel execution

### Debug Mode

```bash
claude run agent:my-agent --debug
```

### Logs

Agent logs are stored in `.claude/logs/`

## Contributing

### Adding New Templates

1. Create template in `templates/`
2. Include comprehensive documentation
3. Add example configuration
4. Test with real scenarios

### Improving Existing Agents

1. Fork agent to `agents/`
2. Make improvements
3. Test thoroughly
4. Update documentation

## Resources

- [Claude Documentation](https://docs.anthropic.com)
- [Agent Best Practices](https://docs.anthropic.com/agents)
- [Project Wiki](./wiki)
- [Support](./support)

## License

This agent system is configured specifically for the Drink Water API project.
Agents and commands should be adapted for other projects as needed.