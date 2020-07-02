package com.example.demo.provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.example.demo.model.Profile;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import ch.qos.logback.classic.Logger;


public class GoogleProviderFactory implements ProviderFactory {

  static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GoogleProviderFactory.class);
   
  static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
  static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

  static final String SCOPE = "openid profile email";
  static final String RESPONSE_TYPE = "code";


  public ProviderResult authentication(final Map<String, String> config) throws Exception {

    final ProviderResult result = new ProviderResult();
    final UriBuilder builder = UriComponentsBuilder.fromHttpUrl(AUTH_URL);
    builder.queryParam("client_id", config.get("client_id"));
    builder.queryParam("redirect_uri", config.get("redirect_uri"));
    builder.queryParam("response_type", RESPONSE_TYPE);
    builder.queryParam("scope", SCOPE);
    
    result.setUri(builder.build());

    LOGGER.debug(result.toString());

    return result;
  }

  public ProviderResult callback(final Map<String, String> config) throws Exception {

    LOGGER.debug("google provider callback {}", config.toString());
    Profile profile = requestProfile(config);

    if(profile == null || profile.getId() == null){
      throw new Exception("Invaild account!");
    }
  
    String accessToken = "test-access-token";//createAccessToken(profile);
    String refreshToken = "test-refresh_token";//createRefreshToken();

    final ProviderResult result = new ProviderResult();
    final UriBuilder builder = UriComponentsBuilder.fromHttpUrl(config.get("redirect_client_uri"));
    builder.scheme("https");
    builder.queryParam("access_token", accessToken);
    builder.queryParam("refresh_token", refreshToken);
    
    result.setUri(builder.build());

    LOGGER.debug("callback: {}", result.toString());

    return result;
  } 

  String requestAccessToken(final Map<String, String> config) throws IOException {

    final String redirectUri = config.get("redirect_uri");
    final String client_id = config.get("client_id");
    final String secret = config.get("secret");
    final String code = config.get("code");
    try {
      final TokenResponse response =
          new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
              new GenericUrl(TOKEN_URL), code)
              .setClientAuthentication(new ClientParametersAuthentication(client_id, secret))
              .setRedirectUri(redirectUri).execute();
      LOGGER.debug("Access token : {}",response.toString());
      
      //verify((String)response.get("id_token"), client_id, secret);
      return response.getAccessToken();
      
    } catch (final Exception e) {
      LOGGER.error("error", e);
    }

    return null;
  }

  Profile requestProfile(Map<String, String> config) throws IOException {

    final String accessToken = requestAccessToken(config);

    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);   
    Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
              "Oauth2").build();
    Userinfo userinfo = oauth2.userinfo().get().execute();

    LOGGER.debug("userinfo : {}", userinfo.toPrettyString());

    Profile profile = new Profile();
    profile.setEmail(userinfo.getEmail());
    profile.setName(userinfo.getName());
    profile.setPicture(userinfo.getPicture());
    profile.setId(userinfo.getId());
    profile.setUsername(userinfo.getEmail());
    profile.setProvider("google");
    return profile;
  }
  
  void verify(String idTokenString, String clientId, String secret) throws Exception {

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
    // Specify the CLIENT_ID of the app that accesses the backend:
    .setAudience(Arrays.asList(clientId))
    // Or, if multiple clients access the backend:
    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
    .build();

    // (Receive idTokenString by HTTPS POST)

    GoogleIdToken idToken = verifier.verify(idTokenString);
    if (idToken != null) {
      Payload payload = idToken.getPayload();

      // Print user identifier
      String userId = payload.getSubject();
      LOGGER.debug("User ID: {}", userId);

      // Get profile information from payload
      String email = payload.getEmail();
      boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
      String name = (String) payload.get("name");
      String pictureUrl = (String) payload.get("picture");
      String locale = (String) payload.get("locale");
      String familyName = (String) payload.get("family_name");
      String givenName = (String) payload.get("given_name");


    } else {
      LOGGER.error("Invalid ID token.");
    }

  }
}