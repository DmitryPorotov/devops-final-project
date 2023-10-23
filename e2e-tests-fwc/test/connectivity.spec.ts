import { login } from "./login";
import { WebSocket } from 'ws';


describe('connect', function () {
    test('login to backend and sand a test message through ws', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const user = await login({email: 'a@b.com', password: "123456781"});

                const webSocket = new WebSocket(`ws://127.0.0.1:3001?_token=${user.token}`, "events");
                webSocket.onmessage = function (event) {
                    const json = JSON.parse(event.data as string);
                    try {
                        expect(json.action).toBe('hello');
                    } catch (e) {
                        reject(e);
                    }
                    resolve();
                };
                webSocket.onopen = (event) => {
                    webSocket.send('{"action": "hello", "type": "test", "userId": 0}')
                };
            }
            catch (e) {
                reject(e)
            }
        })


    })
});

