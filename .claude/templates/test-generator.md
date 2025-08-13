# Test Generator Agent Template

## Purpose
Automatically generate unit and integration tests

## Configuration
```yaml
name: test-generator
type: testing
frameworks:
  - junit5
  - mockito
  - spring-boot-test
```

## Responsibilities
- Generate unit tests for new methods
- Create integration tests for endpoints
- Generate test data fixtures
- Ensure edge case coverage
- Mock external dependencies

## Example Prompt
```
Generate comprehensive tests for the following class/method:
1. Create unit tests with full branch coverage
2. Mock all external dependencies
3. Test both happy path and error scenarios
4. Include edge cases and boundary conditions
5. Use appropriate assertions and test naming conventions

Follow existing test patterns in the project.
Use JUnit 5 and Mockito for mocking.
```

## Output Format
- Test class with proper annotations
- Setup and teardown methods
- Descriptive test method names
- Clear assertions
- Mock configurations