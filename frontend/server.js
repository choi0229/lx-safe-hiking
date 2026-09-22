const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 3000 });

wss.on('connection', (ws) => {
    console.log('클라이언트가 연결되었습니다.');

    ws.on('message', (message) => {
        console.log(`수신된 메시지: ${message}`);

        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(message);
            }
        });
    });

    ws.on('close', () => {
        console.log('클라이언트가 연결을 끊었습니다.');
    });
});

console.log('WebSocket 서버가 실행 중입니다.');
