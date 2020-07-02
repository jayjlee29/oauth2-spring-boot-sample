package com.example.demo.provider;

import java.util.Map;

public interface ProviderFactory {
  
  public ProviderResult authentication(Map<String, String> config) throws Exception;

  public ProviderResult callback(Map<String, String> config) throws Exception;
}