import { login} from "./login"
import { WebSocket } from "ws"
import {send, sleep} from "./utility";
import settings from './settings'

const wsUrl = `${settings.host}:${settings.port}${settings.wsPath}`;

describe('single_server_lobby', function () {
    let user;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user = res;
        })
    });

    test('lobby_create', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const webSocket = new WebSocket(`ws://${wsUrl}?_token=${user.token}`);
                const messageId = "1";
                webSocket.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        if (json.messageId !== messageId) return;
                        expect(json.body.type).toBe('create');
                    }
                    catch (e) {
                        reject(e);
                    }
                    webSocket.close();
                    resolve();
                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(`{"type": "chat", "userId": 1 ,"messageId": "${messageId}", "lobbyId": 5, "body":{"type":"create"}}`);
                });
            } catch (e) {
                reject(e)
            }
        })
    });

    test('lobby_join', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const messageId = "2";
                const user2 = await login({email: 'b@b.com', password: "12345678"});
                expect(user2.token).toBeDefined();
                await send('/lobby/5/join', '{}', user2.token, 'PATCH');

                const webSocket = new WebSocket(`ws://${wsUrl}?_token=${user2.token}`);
                webSocket.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        if (messageId !== json.messageId) return;
                        expect(json.body.type).toBe('join');
                        webSocket.close();
                        resolve();
                    } catch (e) {
                        reject(e);
                    }

                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(
                        JSON.stringify({
                            type: 'chat',
                            userId: 2,
                            messageId,
                            lobbyId: 5,
                            body: {
                                type: 'join'
                            }
                        })
                    )
                });
            } catch (e) {
                reject(e)
            }
        })
    });

    test('lobby_kick', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const messageId = "3";
                const messageId2 = "4";
                const messageId3 = "5";
                const user2 = await login({email: 'b@b.com', password: "12345678"});
                expect(user2.token).toBeDefined();
                await send('/lobby/5/join', '{}', user2.token, 'PATCH');

                const webSocket = new WebSocket(`ws://${wsUrl}?_token=${user2.token}`);
                webSocket.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        // console.log(event.data)
                        if (messageId === json.messageId) {
                            expect(json.body.type).toBe('join');
                        }
                        if (messageId3 === json.messageId) {
                            expect(json.body.type).toBe('kick');
                            webSocket.close()
                            resolve()
                        }
                    }
                    catch (e) {
                        reject(e);
                    }
                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(`{"type": "chat", "userId": 2 , "messageId": "${messageId}", "lobbyId": 5, "body":{"type":"join"}}`)
                });

                const webSocket2 = new WebSocket(`ws://${wsUrl}?_token=${user.token}`);

                webSocket2.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        if (messageId2 === json.messageId) {
                            sleep(10).then(()=>{
                                webSocket2.send(`{"type": "chat", "userId": 1 , "messageId": "${messageId3}",`
                                    + ` "lobbyId": 5, "body":{"type":"kick", "to": [2], "body": "kicked"}}`);
                                webSocket2.close()
                            })
                        }
                    }
                    catch (e) {
                        reject(e);
                    }
                });
                webSocket2.addEventListener('open', (event) => {
                    webSocket2.send(`{"type": "chat", "userId": 1 ,"messageId": "${messageId2}", "lobbyId": 5, "body":{"type":"join"}}`)
                });
            } catch (e) {
                reject(e)
            }
        })
    });

    test('lobby_leave', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const messageId = "6";
                const messageId2 = "7";
                const user2 = await login({email: 'b@b.com', password: "12345678"});
                expect(user2.token).toBeDefined();
                await send('/lobby/5/join', '{}', user2.token, 'PATCH');

                const webSocket = new WebSocket(`ws://${wsUrl}?_token=${user2.token}`);
                webSocket.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        // console.log(json)
                        if (messageId === json.messageId) {
                            expect(json.body.type).toBe('join');
                            webSocket.send(`{"type": "chat", "userId": 2 , "messageId": "${messageId2}", "lobbyId": 5, "body":{"type":"leave"}}`)
                        }
                        if (messageId2 === json.messageId) {
                            expect(json.body.type).toBe('leave');
                            webSocket.close();
                            resolve()
                        }
                    }
                    catch (e) {
                        reject(e);
                    }
                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(`{"type": "chat", "userId": 2 , "messageId": "${messageId}", "lobbyId": 5, "body":{"type":"join"}}`)
                });

            } catch (e) {
                reject(e)
            }
        })
    });

    test('lobby_message', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const messageId = "8";
                const messageId2 = "9";
                const messageId3 = "10";
                const user2 = await login({email: 'b@b.com', password: "12345678"});
                expect(user2.token).toBeDefined();
                await send('/lobby/5/join', '{}', user2.token, 'PATCH');

                const webSocket = new WebSocket(`ws://${wsUrl}?_token=${user2.token}`);
                webSocket.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        // console.log(event.data)
                        if (messageId === json.messageId) {
                            expect(json.body.type).toBe('join');
                        }
                        if (messageId3 === json.messageId) {
                            expect(json.body.type).toBe('message');
                            expect(json.body.body).toBe('hello');
                            webSocket.close();
                            resolve()
                        }
                    } catch (e) {
                        reject(e);
                    }

                });
                webSocket.addEventListener('open', (event) => {
                    webSocket.send(`{"type": "chat", "userId": 2 ,"messageId": "${messageId}", "lobbyId": 5, "body":{"type":"join"}}`)
                });

                const webSocket2 = new WebSocket(`ws://${wsUrl}?_token=${user.token}`);

                webSocket2.addEventListener('message', function (event) {
                    const json = JSON.parse(event.data as string);
                    if (messageId2 === json.messageId) {
                        sleep(10).then(()=>{
                            webSocket2.send(`{"type": "chat", "userId": 1 , "messageId": "${messageId3}",`
                                + ` "lobbyId": 5, "body":{"type":"message", "to": [], "body": "hello"}}`);
                            webSocket2.close();
                        })
                    }

                });
                webSocket2.addEventListener('open', (event) => {
                    webSocket2.send(`{"type": "chat","userId": 1 ,"messageId": "${messageId2}", "lobbyId": 5, "body":{"type":"join"}}`)
                });
            } catch (e) {
                reject(e)
            }
        })
    })
});
