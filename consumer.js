const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

const jwtToken = process.argv[2];
const chatRoom = process.argv[3];

if (!jwtToken || !chatRoom) {
  console.error('Usage: node consumer.js <JWT_TOKEN> <chatRoom>');
  process.exit(1);
}

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
        Authorization: `Bearer ${jwtToken}`,
      },
    }),
  onConnect: () => {
    console.log('[CONSUMER CONNECTED]');

    consumerClient.subscribe(`/topic/chatroom.${chatRoom}`, (message) => {
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
