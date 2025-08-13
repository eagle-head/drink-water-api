# Code Reviewer Agent Template

## Purpose
Automated code review for pull requests and commits

## Configuration
```yaml
name: code-reviewer
type: quality-assurance
triggers:
  - pre-commit
  - pull-request
```

## Responsibilities
- Check code style and formatting
- Identify potential bugs
- Suggest performance improvements
- Verify test coverage
- Check for security vulnerabilities

## Example Prompt
```
Review the following code changes for:
1. Code quality and best practices
2. Potential bugs or edge cases
3. Performance considerations
4. Security implications
5. Test coverage adequacy

Focus on Java/Spring Boot patterns and conventions.
Provide actionable feedback with specific line references.
```

## Integration Points
- Git hooks
- CI/CD pipeline
- PR comments
- IDE integration