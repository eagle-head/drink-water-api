# Refactoring Assistant Agent Template

## Purpose
Identify and execute code refactoring opportunities

## Configuration
```yaml
name: refactoring-assistant
type: code-improvement
patterns:
  - extract-method
  - rename-variable
  - introduce-parameter-object
  - replace-conditional-with-polymorphism
```

## Responsibilities
- Identify code smells
- Suggest refactoring patterns
- Execute safe refactorings
- Maintain backwards compatibility
- Update tests after refactoring

## Example Prompt
```
Analyze the following code for refactoring opportunities:
1. Identify code smells (long methods, duplicate code, etc.)
2. Suggest appropriate refactoring patterns
3. Consider SOLID principles
4. Maintain existing functionality
5. Update affected tests

Priority: readability > performance > brevity
Preserve all existing public APIs.
Follow project coding standards.
```

## Refactoring Catalog
- Extract Method
- Inline Method
- Extract Variable
- Inline Variable
- Extract Class
- Move Method
- Rename Method/Variable/Class
- Replace Magic Number with Constant
- Decompose Conditional