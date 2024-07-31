import { login} from "./login";
import {sleep, WebSocketWrap} from "./utility";
import settings from './settings'

describe('single_server_game_setup', function () {
    let user;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user = res;
        })
    });

    test('join_game', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const sock = new WebSocketWrap(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${user.token}`);
                await sock.open();
                console.log('after open');
                const messageId = String(Math.random());
                const [createdLobbyMsg] = await sock.send({
                    "type": "chat",
                    "userId": user.id,
                    "messageId": messageId,
                    "lobbyId": 4,
                    "body":{"type":"create"}
                });
                console.log("after lobby create");
                expect(createdLobbyMsg.body.type).toBe('create');
                const messageId1 = String(Math.random());
                await sleep(1000);
                const [createdGameMsg] = await sock.send({
                    type: 'action',
                    lobbyId: 4,
                    userId: user.id,
                    messageId: messageId1,
                    action: 'create_game',
                    isRandomHouses: false
                });
                console.log("after game create");
                expect(createdGameMsg.action).toBe('create_game');
                const [resp] = await sock.send({
                    messageId : "123",
                    userId: user.id,
                    action: "join_game",
                    name: 'my name',
                    lobbyId: 4,
                    type: "action",
                    joinAs: "lion"
                } as any);
                expect(resp.gameSettings).toBeDefined();
                resolve()
            }
            catch (e) {
                console.log("in catch", e);
                reject(e)
            }
        })
    });
});
