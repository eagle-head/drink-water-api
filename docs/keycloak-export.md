# Exporting and Copying Keycloak Configuration

This document describes how to export the entire Keycloak configuration to a JSON file and copy it outside the container.

## 1. Ensure the Keycloak Container is Running

The container needs to be running so we can execute commands inside it. To check:

```sh
docker container ls -a
```

If the container **is not** running, start it:

```sh
docker container start <CONTAINER_NAME>
```

> Replace `<CONTAINER_NAME>` with the correct name of your Keycloak container.

---

## 2. Exporting Keycloak Configuration

To export the entire configuration to a single JSON file inside the container, run:

```sh
docker container exec -it <CONTAINER_NAME> /opt/keycloak/bin/kc.sh export --file /tmp/keycloak-realms.json
```

This will create the file `/tmp/keycloak-realms.json` inside the container.

---

## 3. Copying the File Outside the Container

Now, copy the exported file to your local environment. For example, to save it in the `src/main/resources` folder of your project:

```sh
docker container cp <CONTAINER_NAME>:/tmp/keycloak-realms.json ./src/main/resources/keycloak-realms.json
```

This ensures that the export file is available in the local filesystem, ready for use.

---

## 4. (Optional) Remove the Exported File from the Container

If you no longer need the file inside the container, you can delete it to free up space:

```sh
docker container exec -it <CONTAINER_NAME> rm -f /tmp/keycloak-realms.json
```

---

Now, the **`keycloak-realms.json`** file is saved in your project and ready to be reused or imported into another Keycloak environment!