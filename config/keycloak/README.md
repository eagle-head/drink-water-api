# Keycloak Realm Configuration

Exported from **Keycloak 26.5.4** (`quay.io/keycloak/keycloak:26.5.4`).

## File

| File                    | Description                                        |
| ----------------------- | -------------------------------------------------- |
| `drinkwater-realm.json` | Full realm export including users and client roles |

## Realm: drinkwater

### Users

| Username           | Email                     | Roles                                                      |
| ------------------ | ------------------------- | ---------------------------------------------------------- |
| `admin-drinkwater` | `admin@drinkwater.com.br` | `realm-management:realm-admin` (full realm administration) |

### Import

To auto-import on container startup, mount the file and add `--import-realm`:

```yaml
auth_keycloak:
  volumes:
    - ./config/keycloak/drinkwater-realm.json:/opt/keycloak/data/import/drinkwater-realm.json:ro
  command: start --import-realm
```

> **Note:** Re-export after any realm changes to keep this file in sync.
> Use: `docker exec auth_keycloak /opt/keycloak/bin/kc.sh export --dir /tmp/export --realm drinkwater --users realm_file`
