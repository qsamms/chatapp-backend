package com.chatappbackend.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.chatappbackend.document.MessageDocument;
import com.chatappbackend.dto.chatroom.ChatRoomDTO;
import java.io.IOException;
import java.util.List;
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

  public List<MessageDocument> searchMessages(String text, List<ChatRoomDTO> chatRooms) {
    try {
      List<FieldValue> roomIds =
          chatRooms.stream()
              .map(r -> r.getId())
              .map(uuid -> FieldValue.of(uuid.toString()))
              .toList();
      var response =
          elasticClient.search(
              s ->
                  s.index("messages")
                      .query(
                          q ->
                              q.bool(
                                  b ->
                                      b.filter(
                                              f ->
                                                  f.terms(
                                                      t ->
                                                          t.field("room")
                                                              .terms(v -> v.value(roomIds))))
                                          .must(
                                              m ->
                                                  m.match(
                                                      mt ->
                                                          mt.field("text")
                                                              .query(text)
                                                              .fuzziness("AUTO"))))),
              MessageDocument.class);
      return response.hits().hits().stream().map(Hit::source).toList();
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }
}
