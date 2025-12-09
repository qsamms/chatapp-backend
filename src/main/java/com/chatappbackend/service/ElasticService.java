package com.chatappbackend.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.chatappbackend.document.MessageDocument;
import java.io.IOException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ElasticService {

  private final ElasticsearchClient elasticClient;

  public ElasticService(ElasticsearchClient elasticClient) {
    this.elasticClient = elasticClient;
  }

  @Async
  public void indexMessage(MessageDocument messageDocument) {
    IndexRequest<MessageDocument> request =
        IndexRequest.of(i -> i.index("messages").document(messageDocument));

    try {
      IndexResponse response = elasticClient.index(request);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
