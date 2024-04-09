import LoginData from './LoginData';
import sleep from './sleep'

class Main {
    private readonly apiBaseUrl = "localhost:3001/api/v1/";

    private async loginEveryone() {
        for (const p of LoginData) {
            const resp = await fetch("http://" + this.apiBaseUrl + "auth/login", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    email: p.username,
                    password: p.password,

                }),
            });
            const result = await resp.json();
            p.id = result.id;
            p.token = result.token;
            p.ws = new WebSocket(`ws://localhost:3001/ws?_token=${p.token}`);
        }
        return LoginData
    }

    async start() {
        const players = await this.loginEveryone();
        await sleep(.5);
        for (const p of players) {
            await fetch(`http://${this.apiBaseUrl}lobby/4/join`, {
                method: "PATCH",
                headers: {"Authorization": "Bearer " + p.token},
            })
        }
        for (const p of players) {
            p.ws?.addEventListener('message',(m)=> {
                console.log(m.data);
            });
            p.ws?.send(JSON.stringify({
                userId: p.id,
                type: 'chat',
                lobbyId: 4,
                body: {
                    to: [],
                    type: 'join',
                    body: ''
                }
            }));
        }
        await sleep(1);
        console.log('before creating game')
        players[0].ws?.send(JSON.stringify({
            userId: players[0].id,
            type: 'action',
            lobbyId: 4,
            action: 'create_game',
            isRandomHouses: false
        }));
        await sleep(1);
        for (const p of players) {
            p.ws?.send(JSON.stringify({
                userId: p.id,
                type: 'action',
                lobbyId: 4,
                action: "join_game",
            }));
        }

    }

    getPlayers() {
        return LoginData;
    }
}

export default Main;