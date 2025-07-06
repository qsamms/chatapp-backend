const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

const jwtToken = process.argv[2];
const chatRoom = process.argv[3];

if (!jwtToken || !chatRoom) {
  console.error('Usage: node consumer.js <JWT_TOKEN> <chatRoom>');
  process.exit(1);
}

const producerClient = new Client({
  debug: (str) => {
    console.log('[PRODUCER DEBUG]', str);
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
    console.log('[PRODUCER CONNECTED]');
    setInterval(() => {
      producerClient.publish({
        destination: '/app/sendMessage',
        body: JSON.stringify({
          content: `Ping at ${new Date().toISOString()}`,
          chatRoomId: chatRoom,
        }),
        headers: { 'content-type': 'application/json' },
      });
    }, 1000);
  },
  onStompError: (frame) => {
    console.error('[PRODUCER STOMP ERROR]', frame.headers['message'], frame.body);
  },
  onDisconnect: () => {
    console.log('[PRODUCER DISCONNECTED]');
  },
  onWebSocketClose: (evt) => {
    console.log('[PRODUCER WS CLOSED]', evt.code, evt.reason);
  },
  onWebSocketError: (evt) => {
    console.error('[PRODUCER WS ERROR]', evt);
  },
});

producerClient.activate();
