package com.chatappbackend.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

  @Bean
  public ElasticsearchClient elasticsearchClient() {
    String elasticUrl = System.getenv("ELASTIC_URL");
    String username = System.getenv("ELASTIC_USERNAME");
    String password = System.getenv("ELASTIC_PASSWORD");

    BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(username, password));

    RestClient restClient =
        RestClient.builder(HttpHost.create(elasticUrl))
            .setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credsProvider))
            .build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(mapper);
    RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);
    return new ElasticsearchClient(transport);
  }
}
