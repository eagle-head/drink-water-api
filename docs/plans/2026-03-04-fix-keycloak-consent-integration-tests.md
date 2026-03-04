# Fix Keycloak "Client requires user consent" in Integration Tests

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix the 11 failing integration tests in `UserApiIT` and `WaterIntakeApiIT` that return `"Client requires user consent"` when acquiring a Keycloak token via password grant.

**Architecture:** The test realm (`keycloak-realms.json`) has `consentRequired: true` on the `drinkwaterapp` client, which blocks the password grant from working programmatically. The production realm (`drinkwater-realm.json`) also has `consentRequired: true`. The correct fix is to disable consent on the test client, since consent is a browser UX feature (OAuth2 consent screen) and is incompatible with the `directAccessGrants` (password grant) flow used in tests.

**Tech Stack:** Keycloak 26.5.4, Testcontainers, Spring Boot, JUnit 5

---

## Root Cause Analysis

**Error:** All 11 tests fail with:

```
400 Bad Request: {"error":"invalid_client","error_description":"Client requires user consent"}
```

**Cause:** In `src/test/resources/keycloak-realms.json`, line 899:

```json
"consentRequired": true
```

When `consentRequired` is `true`, Keycloak requires user interaction (a browser consent screen) to grant scopes — even when `directAccessGrantsEnabled` is `true`. The password grant (used by `TestAuthProvider`) has no way to present a consent screen, so Keycloak rejects the request.

**Why this is not a workaround:** `consentRequired` controls the OAuth2 consent screen, which only makes sense in interactive flows (Authorization Code Flow with a browser). The password grant (`directAccessGrantsEnabled`) is inherently non-interactive. Having both enabled is a contradictory configuration. The fix aligns the configuration with actual usage.

---

## Affected Tests (11 errors)

**`UserApiIT`** — 4 errors:

- `givenValidToken_whenGetUserInfo_thenReturnUserDetails`
- `givenValidToken_whenCreateUser_thenReturnCreatedUser`
- `givenValidToken_whenUpdateUser_thenReturnUpdatedUser`
- `givenValidToken_whenDeleteUser_thenReturnNoContent`

**`WaterIntakeApiIT`** — 7 errors:

- `givenValidToken_whenCreateWaterIntake_thenReturnCreatedWaterIntake`
- `givenValidToken_whenDeleteWaterIntake_thenReturnNoContent`
- `givenValidTokenAndNonExistentId_whenGetWaterIntake_thenReturnNotFound`
- `givenValidToken_whenGetWaterIntakeById_thenReturnWaterIntake`
- `givenValidToken_whenUpdateWaterIntake_thenReturnUpdatedWaterIntake`
- `givenValidToken_whenSearchWaterIntakes_thenReturnPaginatedResults`
- `givenDuplicateDateTime_whenCreateWaterIntake_thenReturnBadRequest`

---

### Task 1: Fix `consentRequired` in the test realm

**Files:**

- Modify: `src/test/resources/keycloak-realms.json:899`

**Step 1: Change `consentRequired` from `true` to `false`**

In `src/test/resources/keycloak-realms.json`, line 899, change:

```json
// BEFORE
"consentRequired": true,

// AFTER
"consentRequired": false,
```

**Step 2: Run all integration tests with containers**

```bash
mvn verify -P all-tests 2>&1 | tee test-output.log
```

Expected: BUILD SUCCESS, 0 errors, 0 failures. All 11 previously failing tests must now pass.

**Step 3: Verify unit tests still pass**

```bash
mvn test 2>&1 | tail -5
```

Expected: `Tests run: 600, Failures: 0, Errors: 0, Skipped: 0`

**Step 4: Commit**

```bash
git add src/test/resources/keycloak-realms.json
git commit -m "fix: disable consentRequired in test realm for password grant compatibility"
```

---

## Note on the production realm

`config/keycloak/drinkwater-realm.json` (line 637) also has `consentRequired: true`. If `directAccessGrantsEnabled` is used programmatically in production, the same contradictory configuration exists there. Evaluate whether consent is actually needed in production or should also be disabled — but that is a product decision outside the scope of this test fix.
