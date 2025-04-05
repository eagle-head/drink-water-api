# DrinkWater OAuth Scope Policy and Governance

## Table of Contents

1. [Introduction](#introduction)
2. [Scope Policy Principles](#scope-policy-principles)
3. [Scope Structure](#scope-structure)
   - [Naming Convention](#naming-convention)
   - [Scope Granularity](#scope-granularity)
4. [Scope Catalog](#scope-catalog)
   - [User Scopes](#user-scopes)
   - [Water Intake Scopes](#water-intake-scopes)
   - [Reports and Statistics Scopes](#reports-and-statistics-scopes)
   - [Administration Scopes](#administration-scopes)
5. [Keycloak Implementation](#keycloak-implementation)
   - [Client Scopes Configuration](#client-scopes-configuration)
   - [Protocol Mappers](#protocol-mappers)
   - [Client Configuration](#client-configuration)
6. [Scope Versioning](#scope-versioning)
   - [Versioning Strategy](#versioning-strategy)
   - [Domain-Based Versioning](#domain-based-versioning)
   - [Versioning Implementation](#versioning-implementation)
   - [Deprecation and Migration](#deprecation-and-migration)
7. [Request and Approval Process](#request-and-approval-process)
8. [Auditing and Monitoring](#auditing-and-monitoring)
9. [Scope Revocation](#scope-revocation)
10. [Microservices Integration](#microservices-integration)
11. [Best Practices](#best-practices)
12. [References](#references)

## Introduction

This document establishes the OAuth scope policy for the DrinkWater application, implemented through Keycloak as an
OpenID Connect (OIDC) identity provider. The policy ensures the least privilege principle is consistently applied
throughout the application, granting users and clients only the essential permissions required to perform their
functions.

DrinkWater is a hydration tracking application that allows users to record their water intake and track their hydration
goals. The scope policy described in this document covers all resources and functionalities of the application,
including user management, water intake tracking, and report generation.

## Scope Policy Principles

1. **Least Privilege Principle**: Users and client applications must receive only the essential privileges necessary to
   perform their legitimate functions.

2. **Separation of Responsibilities**: Scopes are defined to clearly separate different areas of responsibility and
   functionality.

3. **Appropriate Granularity**: Scopes are defined with sufficient granularity to allow precise control, but not so
   granular as to become unmanageable.

4. **Comprehensibility**: Scopes are named and described clearly to ensure their purpose is easily understood by
   developers, administrators, and end users.

5. **Auditability**: The assignment, use, and revocation of scopes are logged to facilitate audits and respond to
   security incidents.

6. **Explicit Versioning**: Scopes are explicitly versioned to allow API evolution without breaking existing clients.

## Scope Structure

### Naming Convention

To ensure consistency and clarity, scopes in DrinkWater follow this naming convention:

```
drinkwater:[version]:[domain]:[resource]:[action]
```

Where:

- **drinkwater**: Application prefix to avoid conflicts with other scopes.
- **version**: Version of the scope (v1, v2, etc.).
- **domain**: Main functional area (e.g., user, waterintake, reports).
- **resource**: Specific resource within the domain (e.g., profile, entry).
- **action**: Allowed operation (read, write, create, update, delete).

Examples:

- `drinkwater:v1:user:profile:read`
- `drinkwater:v1:waterintake:entry:create`
- `drinkwater:v2:waterintake:entries:search`

### Scope Granularity

Scopes are defined with sufficient granularity to:

1. **Separate read and write operations**: Clients that only need to read data don't receive write permissions.
2. **Distinguish between create, update, and delete**: Destructive or modifying operations are separated.
3. **Isolate functional domains**: Scopes for user management are separated from scopes for water intake tracking.
4. **Control access to sensitive resources**: Personal data and health information have dedicated scopes.

## Scope Catalog

### User Scopes

| Scope                               | Description                       | Endpoint(s)   |
|-------------------------------------|-----------------------------------|---------------|
| `drinkwater:v1:user:profile:read`   | Read the current user's profile   | GET /users/me |
| `drinkwater:v1:user:profile:write`  | Update the current user's profile | PUT /users    |
| `drinkwater:v1:user:profile:create` | Create a new user profile         | POST /users   |
| `drinkwater:v1:user:profile:delete` | Delete the current user's profile | DELETE /users |

### Water Intake Scopes

| Scope                                      | Description                            | Endpoint(s)                      |
|--------------------------------------------|----------------------------------------|----------------------------------|
| `drinkwater:v1:waterintake:entry:read`     | Read a specific water intake entry     | GET /users/waterintakes/{id}     |
| `drinkwater:v1:waterintake:entry:create`   | Create a new water intake entry        | POST /users/waterintakes         |
| `drinkwater:v1:waterintake:entry:update`   | Update an existing water intake entry  | PUT /users/waterintakes/{id}     |
| `drinkwater:v1:waterintake:entry:delete`   | Delete a water intake entry            | DELETE /users/waterintakes/{id}  |
| `drinkwater:v1:waterintake:entries:search` | Search and filter water intake entries | GET /users/waterintakes?[params] |

## Keycloak Implementation

### Client Scopes Configuration

In Keycloak, OAuth scopes are implemented as Client Scopes. This allows them to be easily assigned to different clients, promoting reuse and consistency.

For each scope in the catalog, you should:

1. Access the **Client Scopes** section in the Keycloak admin panel
2. Create a new Client Scope with a name that matches the defined scope
3. Set the Protocol as OpenID Connect
4. Add a clear description explaining the purpose of the scope
5. Configure the Display Type as "Consent Screen Text" to ensure the user is informed about the scope during consent
6. Add the appropriate Protocol Mappers (detailed below)

### Protocol Mappers

Each Client Scope should have Protocol Mappers configured to ensure that scopes appear correctly in the JWT token:

1. Add a "User Attribute" mapper to map user information relevant to the scope
2. Add an "Audience" mapper to include the client in the token audience
3. Configure a "Scope" mapper to ensure the scope is included in the token

For the scope `drinkwater:v1:user:profile:read`, for example:

```
Name: user-profile-read-scope-mapper
Mapper Type: User Client Role
Client ID: drinkwater-api
Token Claim Name: scope
Claim JSON Type: String
Add to ID token: ON
Add to access token: ON
Add to userinfo: OFF
Multivalued: ON
```

### Client Configuration

To manage scope assignments efficiently, follow these guidelines:

1. **Direct Scope Assignment**: Assign scopes directly to clients based on their specific needs
2. **Default Scopes**: Configure commonly needed scopes as default scopes for appropriate client types
3. **Optional Scopes**: Configure additional scopes as optional, requiring explicit user consent
4. **Consent Management**: Customize consent screens to clearly explain the purpose of each scope

This approach maintains the granularity and auditing benefits of scopes while ensuring efficient management within Keycloak.

## Scope Versioning

### Versioning Strategy

Scope versioning is essential to allow API evolution without breaking existing clients. DrinkWater adopts a straightforward integer versioning approach where each functional domain can evolve independently.

#### Version Increments

- Each scope version uses simple integer values (v1, v2, v3, etc.)
- Any change that affects the behavior, data returned, or implementation of a scope requires a new version
- There are no "minor" versions - each version represents a distinct contract between the API and clients

#### Rules for Version Increment

- Changes in the data structure or format returned
- Addition or removal of fields or capabilities
- Changes in authorization requirements or business logic
- Changes that would cause existing clients to malfunction

### Domain-Based Versioning

The DrinkWater application uses domain-based versioning, which means:

1. **Independent Evolution**: Each domain (user, waterintake, reports, etc.) can evolve at its own pace
2. **Isolated Impact**: Changes in one domain don't force unnecessary changes in others
3. **Reduced Administrative Overhead**: Only version domains that have actually changed
4. **Simplification for Clients**: Clients only need to adapt to changes in domains they use

Example of domain-based versioning with independent scopes:

```
drinkwater:v1:user:profile:read      (user domain remains on v1)
drinkwater:v2:waterintake:entry:read (waterintake domain updated to v2)
drinkwater:v1:reports:daily:read     (reports domain remains on v1)
```

**Important principles**:

1. Different domains can operate on different versions simultaneously
2. Only the scopes that have changed require new versions
3. Clear documentation of active versions per domain
4. Clear communication to developers about domain-specific changes

### Versioning Implementation

When implementing a new version of scopes for a specific domain:

1. **Create new scopes** with the new version (e.g., `drinkwater:v2:waterintake:entry:read`)
2. **Document the differences** between versions
3. **Implement endpoints** that respond to both versions during the transition period
4. **Communicate to clients** about the new versions and deprecation plans
5. **Implement transparent upgrade paths** for users of deprecated scopes

**Important: New capabilities or resources must always start at v1**, even if they belong to a domain with existing v2 scopes. For example, if adding export functionality to the waterintake domain which already has v2 scopes, the new scope would be `drinkwater:v1:waterintake:entries:export`.

#### Example: Version Evolution

**Initial scopes (Year 1)**:
All domains use v1 scopes

```
drinkwater:v1:user:profile:read
drinkwater:v1:waterintake:entry:read
drinkwater:v1:reports:daily:read
```

**Year 2**:
Water intake tracking is enhanced with new features, requiring v2 scopes for that domain only, and a completely new export feature starting at v1

```
drinkwater:v1:user:profile:read          (unchanged)
drinkwater:v2:waterintake:entry:read     (updated to v2)
drinkwater:v2:waterintake:entries:search (new in v2)
drinkwater:v1:waterintake:entries:export (new capability starting at v1)
drinkwater:v1:reports:daily:read         (unchanged)
```

Notice that only the waterintake domain moved to v2 for existing functionality, while other domains remain at v1. The new export functionality starts at v1 despite being in the same domain as v2 scopes. This allows for targeted, isolated changes without affecting unrelated functionality while maintaining a clear versioning history for each individual scope.

### Deprecation and Migration

The following approaches enable smooth transitions between scope versions:

#### Transition Periods

During a predefined period (typically 6 months):

1. Both old and new versions of changed scopes are accepted
2. New clients are directed to use only the new version
3. Existing clients receive deprecation warnings
4. After the transition period, deprecated scopes are no longer accessible

#### User Redirection

When a client attempts to use a deprecated scope after the transition period:

1. The request is rejected with a 403 Forbidden status
2. The user is redirected to a consent screen for the new scope version
3. After accepting the new scope, the client automatically receives the new token
4. This process continues until all clients have migrated to the new version

#### Support Policies

DrinkWater uses transparent support policies:

1. **Clear deprecation timelines**: Communicate exact dates when scopes will be deprecated
2. **Proactive notification**: Notify clients of upcoming changes before they occur
3. **Selective deprecation**: Only deprecate scopes that actually need to change
4. **Documentation**: Maintain clear documentation of all active and deprecated scope versions

#### Code Example: Supporting Multiple Versions with @PreAuthorize

```java
@RestController
@RequestMapping("/users/waterintakes")
public class WaterIntakeController {

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:read') || " +
                 "hasAuthority('SCOPE_drinkwater:v2:waterintake:entry:read')")
    public ResponseEntity<ResponseWaterIntakeDTO> findById(@PathVariable Long id,
                                                         JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);

        // Check if using deprecated v1 scope
        if (token.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_drinkwater:v1:waterintake:entry:read"))) {
            // Log deprecation warning
            log.warn("Client {} using deprecated scope v1", token.getAuthorizedClientRegistrationId());
            // Consider adding a header to inform about deprecation
            // return ResponseEntity.ok().header("X-Scope-Deprecated", "true")...
        }

        var responseDTO = this.waterIntakeService.findByIdAndUserId(id, user.getId());
        return ResponseEntity.ok(responseDTO);
    }

    // v2-only endpoint example
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v2:waterintake:entries:search')")
    public ResponseEntity<List<ResponseWaterIntakeDTO>> search(
            @RequestParam Map<String, String> searchParams,
            JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var results = this.waterIntakeService.search(searchParams, user.getId());
        return ResponseEntity.ok(results);
    }
}
```

This implementation allows both v1 and v2 scopes to be used during the transition period, while notifying clients using deprecated scopes.

## Request and Approval Process

To ensure control and security, we implement a formal process for scope request and approval:

### For Internal Applications

1. The technical lead submits a scope request form specifying:
   - Application name and description
   - Requested scopes and justification
   - Required access duration
   - Impact on user data

2. The security team evaluates the request based on:
   - Least privilege principle
   - Access necessity
   - Application security history

3. After approval:
   - Scopes are configured in Keycloak
   - The development team receives the necessary credentials
   - Approval is documented for audit

### For Partner and Third-Party Applications

1. The partner submits a formal request including:
   - Detailed use case description
   - Technical implementation plan
   - Security and privacy policies
   - Data protection measures

2. The request undergoes a risk assessment considering:
   - Partner reputation and security history
   - Sensitivity of accessed data
   - Compliance with privacy regulations

3. After approval:
   - A formal contract specifying terms of use is established
   - Access is provisioned with limited scope and duration
   - Periodic reviews are scheduled

## Auditing and Monitoring

### Logging and Auditing

All scope-related events are logged for audit purposes:

1. **Logged Events**:
   - Scope assignment to clients
   - Scope revocation
   - Unauthorized access attempts
   - Changes in scope definitions

2. **Log Format**:
   ```json
   {
     "timestamp": "2025-04-02T12:34:56Z",
     "event_type": "scope_assignment",
     "client_id": "mobile-app",
     "user_id": "user123",
     "scope": "drinkwater:v1:waterintake:entry:read",
     "actor": "admin_user",
     "source_ip": "192.168.1.1"
   }
   ```

3. **Log Retention**:
   - Audit logs are kept for 12 months
   - Logs are exported to a SIEM system for analysis
   - Encrypted backups are kept for 24 months

### Monitoring and Alerts

We implement proactive monitoring of scope usage:

1. **Anomaly Detection**:
   - Unusual request rate
   - Suspicious scope usage patterns
   - Requests for unusual data types

2. **Real-time Alerts**:
   - Attempts to use revoked scopes
   - Multiple authentication failures
   - Access from unusual geographic locations

3. **Periodic Reports**:
   - Scope usage by client
   - Access trends over time
   - Recommendations for scope adjustment

## Scope Revocation

In case of suspected compromise or policy violation:

1. **Immediate Revocation**:
   - Remove the scopes from the client in Keycloak
   - Revoke all existing access and refresh tokens
   - Notify the client owner

2. **Cascade Revocation**:
   - Identify and revoke all derived tokens
   - Check all downstream systems
   - Update permissions in related services

3. **Documentation and Remediation**:
   - Document the incident and actions taken
   - Perform root cause analysis
   - Implement measures to prevent recurrence

## Microservices Integration

DrinkWater's architecture is based on microservices, and scope implementation must be consistent across all services:

### Token Validation in Microservices

Each microservice must implement token and scope validation:

1. **Signature Verification**:
   - Validate the JWT token signature using Keycloak's public key

2. **Claims Validation**:
   - Check token validity (exp, iat, nbf)
   - Confirm issuer (iss) and audience (aud)
   - Validate that the subject (sub) corresponds to the expected user

3. **Scope Verification**:
   - Extract scopes from the token
   - Check if the necessary scope is present
   - Deny access if required scopes are absent

## Best Practices

### Endpoint Protection

1. **Always verify scopes before granting access**:
   - Don't rely on authentication alone
   - Implement verification at all security layers

2. **Use tested libraries for token validation**:
   - For Spring: Spring Security OAuth2 Resource Server
   - For Express.js: express-oauth2-jwt-bearer
   - For .NET: Microsoft.AspNetCore.Authentication.JwtBearer

3. **Implement client-based rate limiting**:
   - Establish limits per client and per user
   - Apply throttling to prevent abuse

### Scope Design

1. **Keep scopes granular but manageable**:
   - Too many fine-grained scopes increase complexity
   - Too few broad scopes compromise the least privilege principle
   - Find balance with focus on actual usage patterns

2. **Avoid scope overlap**:
   - Each operation should be clearly covered by a specific scope
   - Avoid ambiguity about which scope is needed

3. **Favor direct scope assignment over role-based grouping**:
   - Assign scopes directly to clients for maximum granularity and control
   - Use role-based grouping only when necessary for administrative convenience
   - Always prioritize the principle of least privilege

## References

1. OAuth 2.0 Authorization Framework (RFC 6749): https://tools.ietf.org/html/rfc6749
2. OpenID Connect Core 1.0: https://openid.net/specs/openid-connect-core-1_0.html
3. OAuth 2.0 Security Best Current Practice: https://tools.ietf.org/html/draft-ietf-oauth-security-topics
4. Keycloak Documentation: https://www.keycloak.org/documentation
5. Spring Security OAuth 2.0 Resource Server: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
6. Least Privilege Principle: https://csrc.nist.gov/glossary/term/least_privilege