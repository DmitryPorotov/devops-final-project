import { login, send, sleep } from "./login"
import { WebSocket } from "ws"

describe('basic_game', function () {
    let user;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user = res;
        })
    })

    test('game_create', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const webSocket = new WebSocket(`ws://127.0.0.1:3001?_token=${user.token}`);
                const messageId = String(Math.random());
                const messageId1 = String(Math.random());
                webSocket.addEventListener('message', function (event) {
                    const json = JSON.parse(event.data as string);
                    if (json.messageId === messageId) {
                        expect(json.body.type).toBe('create');
                        webSocket.send(`{"type": "chat", "messageId": "${messageId1}", "lobbyId": 2, "body":{"type":"create"}}`)
                    }
                    else if (json.messageId === messageId1) {

                    }
                    resolve();
                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(`{"type": "chat", "messageId": "${messageId}", "lobbyId": 2, "body":{"type":"create"}}`)
                });
            } catch (e) {
                reject(e)
            }
        })
    })
})