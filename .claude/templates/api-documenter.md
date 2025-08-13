# API Documenter Agent Template

## Purpose
Generate and maintain API documentation

## Configuration
```yaml
name: api-documenter
type: documentation
formats:
  - openapi
  - markdown
  - postman
```

## Responsibilities
- Extract API endpoints from code
- Generate OpenAPI specifications
- Create request/response examples
- Document authentication requirements
- Generate Postman collections
- Update README with API changes

## Example Prompt
```
Document the following API endpoint:
1. Extract method signature and annotations
2. Identify request/response DTOs
3. Generate OpenAPI specification
4. Create curl examples
5. Document error responses
6. Add authentication requirements

Format output as OpenAPI 3.0 specification.
Include realistic example data.
Document all possible response codes.
```

## Output Locations
- `/docs/api/` - API documentation
- `/src/main/resources/openapi.yaml` - OpenAPI spec
- `/postman/` - Postman collections