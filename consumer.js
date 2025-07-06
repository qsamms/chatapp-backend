const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

const consumerClient = new Client({
  debug: (str) => {
    console.log('[CONSUMER DEBUG]', str);
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 0,
  heartbeatOutgoing: 0,
  webSocketFactory: () =>
    new WebSocket('ws://localhost:8080/ws', {
      headers: {
        Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxdWlubmFzZGZhc2RmIiwiaWF0IjoxNzUxNzgyNTU3LCJleHAiOjE3NTE3ODYxNTd9.udWJVkmfFc1r27NUVulMV7nBKQPEOLnhWOeh9SGWSpY',
      },
    }),
  onConnect: () => {
    console.log('[CONSUMER CONNECTED]');

    consumerClient.subscribe('/topic/chatroom.7537b9fe-56a8-4af7-a755-f442953be01f', (message) => {
      console.log('[RECEIVED MESSAGE]', message.body);
    });
  },
  onStompError: (frame) => {
    console.error('[CONSUMER STOMP ERROR]', frame.headers['message'], frame.body);
  },
  onDisconnect: () => {
    console.log('[CONSUMER DISCONNECTED]');
  },
  onWebSocketClose: (evt) => {
    console.log('[CONSUMER WS CLOSED]', evt.code, evt.reason);
  },
  onWebSocketError: (evt) => {
    console.error('[CONSUMER WS ERROR]', evt);
  },
});

consumerClient.activate();
