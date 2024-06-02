import { login} from "./login";
import { WebSocket } from "ws"
import {LoginUserDto, send, sleep, WebSocketWrap} from "./utility";
import settings from './settings'

describe('single_server_basic_game', function () {
    let user: LoginUserDto;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user = res;
        })
    });

/*    test('connection', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const sock = new WebSocketWrap(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${user.token}`);
                console.log('after open socket');
                const reply = await sock.send({
                    messageId: '12345',
                    action: 'hello',
                    type: 'test',
                    userId: user.id
                });
                console.log(reply);
                expect(reply.action).toBe('hello');
                resolve();
            } catch (e) {
                reject(e);
            }
        })
    });*/

    test('game_create', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const openSocket = (u: LoginUserDto) => new Promise<WebSocket & {u: LoginUserDto}>(r => {
                    const webSocket: WebSocket & {u: LoginUserDto} = new WebSocket(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${u.token}`) as any;
                    webSocket.u = u;
                    webSocket.addEventListener('open', () => {
                        r(webSocket)
                    })
                });
                const user2 = await login({email:'b@b.com', password:'12345678'});
                const user3 = await login({email:'admin@b.com', password:'12345678'});
                const user4 = await login({email:'c@b.com', password:'12345678'});
                const user5 = await login({email:'d@b.com', password:'12345678'});
                const user6 = await login({email:'e@b.com', password:'12345678'});
                const webSocket1 = new WebSocket(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${user.token}`);


                const messageId = String(Math.random());
                const messageId1 = String(Math.random());
                const messageId2 = String(Math.random());
                const messageId3 = String(Math.random());
                let isLobbyCreated = false;
                webSocket1.addEventListener('message', function (event) {
                    try {
                        const json = JSON.parse(event.data as string);
                        switch (json.messageId) {
                            case messageId:
                                expect(json.body.type).toBe('create');
                                isLobbyCreated = true;
                                break;
                            case messageId1:
                                expect(json.action).toBe('create_game');
                                everyoneJoin();
                                break;
                            case messageId2:
                                expect(json.gameSettings.players).toBeDefined();
                                break;
                            case messageId3:
                                expect(json.gameSettings.players[0].house).toBeDefined();
                                resolve();
                                break;
                        }
                    } catch (e) {
                        reject(e)
                    }
                    // resolve();
                });
                webSocket1.addEventListener('open', (event) => {
                    webSocket1.send(`{"type": "chat", "userId": 1, "messageId": "${messageId}", "lobbyId": 2, "body":{"type":"create"}}`)
                });
                const waitForLobby = async () => {
                    do {
                        await sleep(5);
                    } while (!isLobbyCreated)
                };
                await waitForLobby();
                const webSocket2 = openSocket(user2);
                const webSocket3 = openSocket(user3);
                const webSocket4 = openSocket(user4);
                const webSocket5 = openSocket(user5);
                const webSocket6 = openSocket(user6);

                const sockets: Array<WebSocket & {u: LoginUserDto}> = await Promise.all([
                    webSocket2,
                    webSocket3,
                    webSocket4,
                    webSocket5,
                    webSocket6,
                ]);
                for (let s of sockets) {
                    const messageId = String(Math.random());
                    s.addEventListener('message', function (event) {
                        const json = JSON.parse(event.data as string);
                        try {
                            if (messageId === json.messageId) {
                                expect(json.body.type).toBe('join');
                            }
                        }
                        catch (e) {
                            reject(e);
                        }
                    });
                    await send('/lobby/2/join', '{}', s.u.token, 'PATCH');
                    s.send(`{"type": "chat", "userId": ${s.u.id}, "messageId": "${messageId}", "lobbyId": 2, "body":{"type":"join"}}`)
                }
                await sleep(1000);

                webSocket1.send(JSON.stringify({
                    type: 'action',
                    lobbyId: 2,
                    userId: 1,
                    messageId: messageId1,
                    action: 'create_game',
                    isRandomHouses: false
                }));
                const otherHouses = {
                    2: "kraken",
                    3: "pufferfish",
                    4: "wolf",
                    5: "moose",
                    6: "rose"
                };
                const everyoneJoin = function() {
                    webSocket1.send(JSON.stringify({
                        type: 'action',
                        userId: user.id,
                        action: 'join_game',
                        lobbyId: 2,
                        messageId: messageId2,
                        joinAs: "lion",
                        name: 'name'
                    }));
                    for (let s of sockets) {
                        const messageId = String(Math.random());
                        s.addEventListener('message', function (event) {
                            const json = JSON.parse(event.data as string);
                            // console.log(json);
                            try {
                                if (messageId === json.messageId) {
                                    expect(json.gameSettings.players).toBeDefined();
                                    if (json.gameSettings.players.length === 6) {
                                        webSocket1.send(JSON.stringify({
                                            messageId: messageId3,
                                            type: 'action',
                                            userId: user.id,
                                            action: 'start_game',
                                            lobbyId: 2,
                                        }))
                                    }
                                }
                            }
                            catch (e) {
                                reject(e);
                            }
                        });
                        s.send(JSON.stringify({
                            type: 'action',
                            userId: s.u.id,
                            action: 'join_game',
                            lobbyId: 2,
                            name: 'my name',
                            messageId,
                            joinAs: otherHouses[s.u.id]
                        }))
                    }
                }
            } catch (e) {
                reject(e)
            }
        })
    })
});
