package com.example.demo.provider;

import java.net.URI;

public class ProviderResult {
  URI url;

  public URI getUri() {
    return url;
  }

  public void setUri(URI url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "ProviderResult [url=" + url + "]";
  }
  
  
}