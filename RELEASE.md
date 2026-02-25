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

## A. Release credentials

You need an account on sonatype which has been added as a publisher for the namespace com.topicus-keyhub. You also need to generate and publish a gpg signing key with which to sign these artifacts, linked to the e-mail address of your sonatype user.

### A.1 Sonatype account
Follow the 'Create an account' steps on https://central.sonatype.org/register/central-portal/#publishing to create a sonatype account. Make sure to note which e-mail address you use. As of february 2026, your useraccount needs to be added to the namespace via a support ticket created by Topicus Security.

### A.2 Gpg key and sign up
The maven gpg plugin is used to sign the artifacts. You can follow the steps described on https://central.sonatype.org/publish/requirements/gpg/ to create a new gpg signing key and upload it to one or more keyservers. You can specify a descrtipe 'Real name' (such as 'maven signing key') but make sure to use the same e-mail address as for your sontaype account, since that will be use to match the key to your account.

Edit your settings.xml to add gpg credentials. For example:

```xml
<profile>
        <id>central</id>
        <activation>
                <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.keyname>0x7B686799</gpg.keyname>
        </properties>
</profile>
```

### A.3 Sonatype publishing credentials
Generate a user token for your sonatype account (https://central.sonatype.org/publish/generate-portal-token/). This will be used as the credentials for the actual publish action.
Note that the username is a generated token identifier, not your sonatype username or e-mail and also not the token 'name' you entered upon creation.

Add the credentials to your settings.xml for a server with id `central` (this id is used in the publishing plugin's configuration in the pom.xml of this project, so it needs to match exactly).
You can use maven password encryption to encrypt the token value if you want:

```xml
<server>
        <id>central</id>
        <username>aBcDe</username>
        <password>{<random numbers and letters>}</password>
</server>
```
