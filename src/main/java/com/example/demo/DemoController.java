package com.example.demo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.provider.GoogleProviderFactory;
import com.example.demo.provider.ProviderFactory;
import com.example.demo.provider.ProviderResult;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Logger;

@RestController
public class DemoController {
  static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DemoController.class);

  @Value("${client.redirect_uri}")
  private String clientRedirectUri;

  @Value("${provider.google.redirect_uri}")
  private String redirectUri;

  @Value("${provider.google.client_id}")
  private String clientId;

  @Value("${provider.google.secret}")
  private String secret;

  @GetMapping("/authentication/{provider}")
  public ResponseEntity authentication(@PathVariable String provider) throws Exception {
    
    ProviderFactory providerFactory = null;
    LOGGER.info("redirect_uri : " + redirectUri);
    switch(provider){
      case "google":
        providerFactory = new GoogleProviderFactory();
        break;
      default: 
    }

    if(providerFactory == null) {
      throw new Exception("invaild provider!!");
    }
    Map<String, String> config = new HashMap();
    config.put("redirect_uri", redirectUri);
    config.put("client_id", clientId);

    ProviderResult result = providerFactory.authentication(config);
    
    URI location = new URI(result.getUri().toString());
    return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    
  }

  @GetMapping("/authentication/callback/{provider}")
  public ResponseEntity callback(@PathVariable String provider, @RequestParam(required=false) Map<String, String> params) throws Exception{
    ProviderFactory providerFactory = null;
    
    LOGGER.debug("{} privder callback parameter {}", provider, params.toString());

    switch(provider){
      case "google":
        providerFactory = new GoogleProviderFactory();
        break;
      default: 
    }

    Map<String, String> config = new HashMap(params);
    config.put("redirect_client_uri", clientRedirectUri);
    config.put("redirect_uri", redirectUri);
    config.put("secret", secret);
    config.put("client_id", clientId);
 
    ProviderResult result = providerFactory.callback(config);
    URI location = result.getUri();
    return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
  }
  
}