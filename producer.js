const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

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
        Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxc2FtbXMxMTExMTExIiwiaWF0IjoxNzUxNzgwMjI2LCJleHAiOjE3NTE3ODM4MjZ9.NaWwB4-7R_KRDrNg3VOO9I0M2uKpYX5hSWXw6IbqEX0',
      },
    }),
  onConnect: () => {
    console.log('[PRODUCER CONNECTED]');
    setInterval(() => {
      producerClient.publish({
        destination: '/app/sendMessage',
        body: JSON.stringify({
          sender: 'tester',
          content: `Ping at ${new Date().toISOString()}`,
          chatRoomId: '7537b9fe-56a8-4af7-a755-f442953be01f',
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
