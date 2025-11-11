# sdk-java
Topicus KeyHub SDK for Java

## Installation
To add the SDK for Java to your maven project, add the following dependency:
```xml
<dependency>
    <groupId>com.topicus-keyhub</groupId>
    <artifactId>sdk-java</artifactId>
    <version>44.0.0</version>
</dependency>
```

## Getting started
To start using the SDK for Java, you need to create a client.
The simplest client you can create does not require authentication, nor authorization.

Below is an example that reads the info resource:
```java
package com.example;

import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.http.KiotaClientFactory;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import com.microsoft.kiota.serialization.SerializationWriterFactoryRegistry;
import com.topicus.keyhub.sdk.KeyHubClient;
import com.topicus.keyhub.sdk.models.simple.VersionInfo;

import okhttp3.OkHttpClient;

public class ReadInfo {
    public static void main(String[] args) {
        // Getting version information does not require authentication/authorization,
        // so use the anonymous authentication provider
        final AnonymousAuthenticationProvider authProvider =
                new AnonymousAuthenticationProvider();

        final OkHttpClient httpClient = KiotaClientFactory.create().build();

        final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(
                authProvider,
                ParseNodeFactoryRegistry.defaultInstance,
                SerializationWriterFactoryRegistry.defaultInstance,
                httpClient);
        adapter.setBaseUrl("https://keyhub.example.com/keyhub/rest/v1");
        final KeyHubClient keyHubClient = new KeyHubClient(adapter);

        // GET /info
        final VersionInfo info = keyHubClient.info().get();
        System.out.println("KeyHub version: " + info.getKeyHubVersion());
        System.out.println("KeyHub contract versions: " + info.getContractVersions());
    }
}
```

The code above creates an `OkHttpRequestAdapter` using an anonymous authentication provider, the default instances for the `ParseNodeFactory` and `SerializationWriterFactory` and an `OkHttpClient`.
This request adapter is used to create the `KeyHubClient`, which is then used to read the info endpoint.

Before you run this code, make sure to set the base URL to the proper URL of your KeyHub server.
When successful, your output should look something like this:
```
KeyHub version: keyhub-44-1
KeyHub contract versions: [81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67]
```
Your actual version numbers may vary.


### Creating a client with authentication (client credentials grant)

Below is an example on how to create a `KeyHubClient` that authenticates to the backend using a client id and secret.
Replace the values of the KeyHub base URL, client id and secret with the correct values for your Topicus KeyHub deployment.

```java
package com.example;

import java.util.stream.Collectors;

import com.microsoft.kiota.http.KiotaClientFactory;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import com.microsoft.kiota.serialization.SerializationWriterFactoryRegistry;
import com.topicus.keyhub.sdk.KeyHubAuthenticationProvider;
import com.topicus.keyhub.sdk.KeyHubClient;
import com.topicus.keyhub.sdk.models.auth.Permission;
import com.topicus.keyhub.sdk.models.client.OAuth2Client;

import okhttp3.OkHttpClient;

public class ReadClientMe {
    public static void main(String[] args) {
        final String keyHubBaseUrl = "https://keyhub.example.com";
        final String clientId = "9bdf985e-013e-44b8-a575-373aa4d60e81";
        final String clientSecret = "<put your secret here>";
		
        // Getting version information does not require authentication/authorization,
        // so use the anonymous authentication provider
        final KeyHubAuthenticationProvider authProvider = 
                new KeyHubAuthenticationProvider(keyHubBaseUrl, clientId, clientSecret);

        final OkHttpClient httpClient = KiotaClientFactory.create().build();

        final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(
                authProvider,
                ParseNodeFactoryRegistry.defaultInstance,
                SerializationWriterFactoryRegistry.defaultInstance,
                httpClient);
        adapter.setBaseUrl(keyHubBaseUrl + "/keyhub/rest/v1");
        final KeyHubClient keyHubClient = new KeyHubClient(adapter);       
		
        // GET /client/me
        final OAuth2Client me = keyHubClient.client().me().get();
        System.out.println("Client id: " + me.getClientId());
        System.out.println("Client permissions: "
                + me.getAccountPermissions().stream().map(Permission::getFull)
                    .collect(Collectors.joining(",\n    ")));
    }
}
```

