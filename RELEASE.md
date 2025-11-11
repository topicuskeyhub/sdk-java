# Updating and releasing Topicus KeyHub SDK for Java

## 1. Updating

### 1.1 Dependencies

Upgrade all versions in `pom.xml` to the latest versions.

Also check https://github.com/microsoft/kiota/releases and update the `kiotaVersion` property of the `kiota-maven-plugin`.

### 1.2 Commit the results

```Shell
git add .
git commit -m "Upgrade dependencies"
git push
```

### 1.3 KeyHub OpenAPI spec

Use the keyhub-openapi-transformer-cli to download and preprocess the openapi spec from a running KeyHub instance.

```Shell
transform --in https://<KEYHUB_HOSTNAME>/keyhub/rest/v1/openapi.json --out /path/to/sdk-java/openapi.json --target java
```

### 1.4 Commit the results

```Shell
git add .
git commit -m "Upgrade to KeyHub 40"
git push
```

## 2. Release

```Shell
mvn release:prepare
mvn release:perform
```
