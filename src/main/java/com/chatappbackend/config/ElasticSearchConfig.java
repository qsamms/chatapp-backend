package com.chatappbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {
  private final String elasticUrl;
  private final String username;
  private final String password;

  public ElasticSearchConfig() {
    elasticUrl = System.getenv("ELASTIC_URL");
    username = System.getenv("ELASTIC_USERNAME");
    password = System.getenv("ELASTIC_PASSWORD");
  }

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(elasticUrl)
        .withBasicAuth(username, password)
        .withConnectTimeout(5000)
        .build();
  }
}
