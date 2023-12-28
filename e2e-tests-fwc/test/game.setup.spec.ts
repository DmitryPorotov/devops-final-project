import { login} from "./login";
import {LoginUserDto, send, sleep, WebSocketWrap} from "./utility";

describe('game_setup', function () {
    let user;

    beforeAll(function () {
        return login({email: 'a@b.com', password: "12345678"}).then(res => {
            user = res;
        })
    });

    test('get_rules', function () {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const sock = new WebSocketWrap(`ws://127.0.0.1:3001?_token=${user.token}`);
                await sock.open();
                console.log('after open');
                const messageId = String(Math.random());
                const createdLobbyMsg = await sock.send({
                    "type": "chat",
                    "userId": user.id,
                    "messageId": messageId,
                    "lobbyId": 2,
                    "body":{"type":"create"}}
                    );
                console.log("after lobby create");
                expect(createdLobbyMsg.body.type).toBe('create');
                const messageId1 = String(Math.random());
                await sleep(500);
                const createdGameMsg = await sock.send({
                    type: 'action',
                    lobbyId: 2,
                    userId: user.id,
                    messageId: messageId1,
                    action: 'create_game',
                    isRandomHouses: false
                });
                console.log("after game create");
                expect(createdGameMsg.gameState.subPhase.subPhase).toBe('addOrder');
                const resp = await sock.send({
                    messageId : "123",
                    userId: user.id,
                    action: "get_rules",
                    lobbyId: 2,
                    type: "action"
                } as any);
                expect(resp.gameRules).toBeDefined()
                resolve()
            }
            catch (e) {
                console.log("in catch", e)
                reject(e)
            }
        })
    });
});
