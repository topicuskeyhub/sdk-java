package nl.topicus.keyhub.sdk;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;

public class KeyHubAuthenticationProvider implements AuthenticationProvider {
	private final String keyHubURI;
	private final String clientId;
	private final String clientSecret;

	private AccessTokenResponse currentToken;
	private Instant tokenExpiration;

	public KeyHubAuthenticationProvider(String keyHubURI, String clientId, String clientSecret) {
		this.keyHubURI = Objects.requireNonNull(keyHubURI);
		this.clientId = Objects.requireNonNull(clientId);
		this.clientSecret = Objects.requireNonNull(clientSecret);
	}

	@Override
	public void authenticateRequest(RequestInformation request, Map<String, Object> additionalAuthenticationContext) {
		Objects.requireNonNull(request);
		fetchAuthenticationTokenIfNeeded();

		request.headers.add("Authorization", "Bearer " + currentToken.getTokens().getAccessToken().getValue());
		request.headers.add("topicus-Vault-session", currentToken.getCustomParameters().get("vaultSession").toString());
	}

	private void fetchAuthenticationTokenIfNeeded() {
		if (currentToken != null && tokenExpiration.isAfter(Instant.now().minusSeconds(60))) {
			return;
		}

		TokenRequest tokenReq = new TokenRequest.Builder(URI.create(keyHubURI + "/login/oauth2/token?authVault=access"),
				new ClientSecretBasic(new ClientID(clientId), new Secret(clientSecret)), new ClientCredentialsGrant())
				.build();
		try {
			TokenResponse resp = TokenResponse.parse(tokenReq.toHTTPRequest().send());
			if (!resp.indicatesSuccess()) {
				throw new GeneralException(resp.toErrorResponse().getErrorObject());
			}
			currentToken = resp.toSuccessResponse();
			long lifetime = currentToken.getTokens().getAccessToken().getLifetime();
			tokenExpiration = Instant.now().plusSeconds(lifetime == 0 ? 3600 : lifetime);
		} catch (GeneralException | IOException e) {
			throw new ApiException(e);
		}
	}
}
