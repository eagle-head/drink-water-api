#!/bin/bash

# Claude Agent System Initialization Script
# Initialize and configure the Claude agent system for the project

set -e

CLAUDE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$CLAUDE_DIR")"

echo "🤖 Claude Agent System Initializer"
echo "=================================="
echo "Project: Drink Water API"
echo "Location: $PROJECT_ROOT"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if already initialized
if [ -f "$CLAUDE_DIR/.initialized" ]; then
    echo -e "${YELLOW}⚠️  System already initialized${NC}"
    read -p "Reinitialize? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 0
    fi
fi

echo "📁 Creating directory structure..."

# Create required directories
mkdir -p "$CLAUDE_DIR/agents"
mkdir -p "$CLAUDE_DIR/commands"
mkdir -p "$CLAUDE_DIR/templates"
mkdir -p "$CLAUDE_DIR/reports"
mkdir -p "$CLAUDE_DIR/logs"
mkdir -p "$CLAUDE_DIR/backups"

echo -e "${GREEN}✓ Directory structure created${NC}"

# Check for required tools
echo ""
echo "🔍 Checking dependencies..."

check_command() {
    if command -v "$1" &> /dev/null; then
        echo -e "${GREEN}✓ $1 found${NC}"
        return 0
    else
        echo -e "${RED}✗ $1 not found${NC}"
        return 1
    fi
}

check_command java
check_command mvn
check_command git

# Initialize git hooks if git is available
if [ -d "$PROJECT_ROOT/.git" ]; then
    echo ""
    echo "🔗 Setting up git hooks..."
    
    # Create pre-commit hook
    cat > "$PROJECT_ROOT/.git/hooks/pre-commit" << 'EOF'
#!/bin/bash
# Claude pre-commit hook

CLAUDE_DIR="$(git rev-parse --show-toplevel)/.claude"

if [ -f "$CLAUDE_DIR/config.yml" ]; then
    echo "Running Claude pre-commit agents..."
    # Add agent execution logic here
fi

exit 0
EOF
    
    chmod +x "$PROJECT_ROOT/.git/hooks/pre-commit"
    echo -e "${GREEN}✓ Git hooks configured${NC}"
fi

# Create helper scripts
echo ""
echo "📝 Creating helper scripts..."

# Create run script
cat > "$CLAUDE_DIR/run.sh" << 'EOF'
#!/bin/bash
# Run Claude agents or commands

CLAUDE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
    echo "Usage: $0 [agent|command] <name>"
    echo ""
    echo "Examples:"
    echo "  $0 agent security-scanner"
    echo "  $0 command full-review"
    exit 1
}

if [ $# -lt 2 ]; then
    usage
fi

TYPE=$1
NAME=$2

case $TYPE in
    agent)
        CONFIG="$CLAUDE_DIR/agents/$NAME.yml"
        if [ -f "$CONFIG" ]; then
            echo "Running agent: $NAME"
            # Add agent execution logic here
        else
            echo "Agent not found: $NAME"
            exit 1
        fi
        ;;
    command)
        CONFIG="$CLAUDE_DIR/commands/$NAME.yml"
        if [ -f "$CONFIG" ]; then
            echo "Running command: $NAME"
            # Add command execution logic here
        else
            echo "Command not found: $NAME"
            exit 1
        fi
        ;;
    *)
        usage
        ;;
esac
EOF

chmod +x "$CLAUDE_DIR/run.sh"

# Create list script
cat > "$CLAUDE_DIR/list.sh" << 'EOF'
#!/bin/bash
# List available agents and commands

CLAUDE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🤖 Available Agents:"
echo "==================="
for agent in "$CLAUDE_DIR/agents"/*.yml; do
    if [ -f "$agent" ]; then
        basename "$agent" .yml | sed 's/^/  - /'
    fi
done

echo ""
echo "📋 Available Commands:"
echo "====================="
for cmd in "$CLAUDE_DIR/commands"/*.yml; do
    if [ -f "$cmd" ]; then
        basename "$cmd" .yml | sed 's/^/  - /'
    fi
done
EOF

chmod +x "$CLAUDE_DIR/list.sh"

echo -e "${GREEN}✓ Helper scripts created${NC}"

# Create initialization marker
date > "$CLAUDE_DIR/.initialized"

# Create quick start guide
cat > "$CLAUDE_DIR/QUICKSTART.md" << 'EOF'
# Claude Agent System - Quick Start

## Available Commands

### List all agents and commands
```bash
.claude/list.sh
```

### Run an agent
```bash
.claude/run.sh agent <agent-name>
```

### Run a command
```bash
.claude/run.sh command <command-name>
```

## Common Tasks

### Run full code review
```bash
.claude/run.sh command full-review
```

### Update API documentation
```bash
.claude/run.sh command api-update
```

### Generate test suite
```bash
.claude/run.sh command test-suite
```

### Security scan
```bash
.claude/run.sh agent security-scanner
```

## Configuration

Edit `.claude/config.yml` to customize global settings.

## Creating New Agents

1. Copy a template from `.claude/templates/`
2. Customize the configuration
3. Save to `.claude/agents/`
4. Test with `.claude/run.sh agent <your-agent>`

## Support

See `.claude/README.md` for full documentation.
EOF

echo ""
echo -e "${GREEN}✅ Initialization complete!${NC}"
echo ""
echo "📚 Quick Start:"
echo "  - List agents/commands: .claude/list.sh"
echo "  - Run agent: .claude/run.sh agent <name>"
echo "  - Run command: .claude/run.sh command <name>"
echo "  - See QUICKSTART.md for more info"
echo ""
echo "Happy coding with Claude! 🚀"