# Claude Code Agent Selection Guide

This document explains how Claude Code should automatically select and orchestrate agents based on user prompts.

## Agent Architecture Overview

The drink-water-api project uses a **Planner-Executor** architecture:

- **🧠 PLANNERS (Opus)**: Analyze current state and design comprehensive strategies
- **🔧 EXECUTORS (Sonnet/Haiku)**: Implement code and configurations based on plans

## Automatic Agent Selection Logic

### 1. Keyword-Based Selection

Claude Code should analyze user prompts for these keywords and select appropriate agents:

#### Security-Related Prompts
**Keywords**: `security`, `oauth2`, `keycloak`, `authentication`, `authorization`, `vulnerability`, `rate limiting`

**Pattern Examples**:
- "Analyze our OAuth2 security" → `security-strategy-architect`
- "Implement rate limiting" → `security-implementation-specialist`
- "Our app has security issues" → `security-strategy-architect` → `security-implementation-specialist`

#### Performance-Related Prompts
**Keywords**: `slow`, `performance`, `optimization`, `database`, `queries`, `caching`, `bottleneck`

**Pattern Examples**:
- "App is slow" → `performance-strategy-analyst` → `jpa-performance-executor`
- "Optimize database queries" → `jpa-performance-executor`
- "Add caching" → `jpa-performance-executor`

#### DevOps-Related Prompts
**Keywords**: `cicd`, `pipeline`, `deployment`, `github actions`, `docker`, `kubernetes`

**Pattern Examples**:
- "Setup CI/CD" → `devops-infrastructure-planner` → `devops-pipeline-executor`
- "Create Docker optimization" → `container-optimization-executor`
- "Add monitoring" → `monitoring-implementation-specialist`

#### Database-Related Prompts
**Keywords**: `database`, `migration`, `flyway`, `schema`, `sql`

**Pattern Examples**:
- "Database migration" → `database-migration-architect` → `database-migration-executor`
- "Schema change" → `database-migration-architect` → `database-migration-executor`

#### API Documentation Prompts
**Keywords**: `api`, `documentation`, `openapi`, `swagger`, `postman`

**Pattern Examples**:
- "Document API" → `api-design-strategist` → `api-documentation-generator`
- "Create OpenAPI spec" → `api-documentation-generator`

#### Testing Prompts
**Keywords**: `testing`, `tests`, `junit`, `mockito`, `coverage`

**Pattern Examples**:
- "Write tests" → `test-implementation-specialist`
- "Improve test coverage" → `test-implementation-specialist`

### 2. Task Complexity Analysis

#### Analysis Tasks → PLANNERS (Opus)
- "Analyze..." → Select appropriate planner
- "Review..." → Select appropriate planner
- "Design strategy for..." → Select appropriate planner
- "What's wrong with..." → Select appropriate planner

#### Implementation Tasks → EXECUTORS (Sonnet/Haiku)
- "Implement..." → Select appropriate executor
- "Create..." → Select appropriate executor
- "Add..." → Select appropriate executor
- "Fix..." → Select appropriate executor

#### Complex Tasks → PLANNER → EXECUTOR Chain
- "Analyze and fix..." → Planner first, then corresponding executor
- "Setup and optimize..." → Planner first, then corresponding executor
- "Review and implement..." → Planner first, then corresponding executor

### 3. Agent Orchestration Rules

#### Sequential Execution (Planner → Executor)
When user requests both analysis AND implementation:

```
User: "Analyze our OAuth2 security and implement rate limiting"
Flow: security-strategy-architect → security-implementation-specialist
```

#### Parallel Execution (Multiple Independent Tasks)
When user requests multiple unrelated tasks:

```
User: "Add monitoring and optimize Docker containers"
Flow: monitoring-implementation-specialist + container-optimization-executor
```

#### Context-Aware Selection
When user provides specific context:

```
User: "Our database queries are slow, what should we do?"
Flow: performance-strategy-analyst (analysis) → jpa-performance-executor (implementation)
```

## Agent Metadata Reference

Each agent contains metadata for automatic selection:

```yaml
keywords: [list of relevant keywords]
triggers: [list of natural language triggers]
agent_type: planner|executor
follows_up_with: next_agent_name  # For planners
planned_by: planner_agent_name    # For executors
```

## Selection Priority Rules

1. **Exact Trigger Match**: If user prompt matches a specific trigger, select that agent
2. **Keyword Density**: Count keyword matches and select agent with highest relevance
3. **Task Type**: Determine if task requires planning, execution, or both
4. **Context Awareness**: Consider project-specific context (Spring Boot, OAuth2, etc.)
5. **Default Chain**: When in doubt, prefer planner → executor chains for complex tasks

## Example Decision Tree

```
User Prompt: "Our API is slow and needs better documentation"

Step 1: Identify keywords
- "slow" → performance domain
- "documentation" → API documentation domain

Step 2: Determine task type
- "is slow" → analysis needed → planner
- "needs documentation" → implementation task → executor

Step 3: Select agents
- performance-strategy-analyst (analyze slowness)
- api-documentation-generator (create docs)

Step 4: Execution order
- Parallel execution (independent tasks)
```

## Integration with Claude Code

Claude Code should:

1. **Parse user prompts** for keywords and intent
2. **Match against agent metadata** (keywords, triggers)
3. **Determine execution strategy** (sequential/parallel)
4. **Select appropriate models** based on agent specifications
5. **Execute in correct order** with proper context passing

This allows users to simply describe what they want in natural language, and Claude Code will automatically select the right specialists and execution strategy.