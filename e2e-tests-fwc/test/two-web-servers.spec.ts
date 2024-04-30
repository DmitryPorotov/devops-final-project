import { login} from "./login";
import {LoginUserDto, send, sleep, WebSocketWrap} from "./utility";
import settings from './settings'

describe('two_servers_chat', function () {
    let user1: LoginUserDto;
    let user2: LoginUserDto;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user1 = res;
            return login({email: 'b@b.com', password: "12345678"}, settings.port2)
        })
            .then(res => {
                user2 = res;
            })
    });

    test('send_message', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const con1 = new WebSocketWrap(`ws://${settings.host}:${settings.port}/ws?_token=${user1.token}`);
                const con2 = new WebSocketWrap(`ws://${settings.host}:${settings.port2}/ws?_token=${user2.token}`);

                await con1.open();
                console.log('after con1 open');
                await con2.open();
                console.log('after con2 open');
                const [createdLobbyMsg] = await con1.send({
                    "type": "chat",
                    "userId": user1.id,
                    "lobbyId": 2,
                    "body":{"type":"create"}
                });
                expect(createdLobbyMsg.body.type).toBe('create');
                await send('/lobby/2/join', '{}', user2.token, 'PATCH', settings.port2);
                console.log('after user2 http join');
                const joinReply = await con2.send({
                    "type": "chat",
                    "userId": user2.id,
                    "lobbyId": 2,
                    messageId: "" + Math.random(),
                    "body":{"type":"join"}
                });
                console.log(joinReply);
                expect(joinReply[joinReply.length-1].body.type).toBe('join');

                const chatMessageId = "" + Math.random();
                con1.onMessage((msg) => {
                    console.log(msg);
                    if (msg.messageId === chatMessageId) {
                        resolve()
                    }
                }, e => reject(e));

                await con2.send({
                    messageId: chatMessageId,
                    lobbyId: 2,
                    userId: user2.id,
                    type: 'chat',
                    body: {
                        type: 'message',
                        body: 'hello',
                        to: []
                    }
                });
            }
            catch (e) {
                reject(e);
            }
        });

    });
});