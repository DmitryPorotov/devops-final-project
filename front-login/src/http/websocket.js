import Api from "./api";

// export interface MessageInterface {
//     from: number;
//     type: 'chat' | 'action';
//     lobbyId: number;
//     body: ChatMessageInterface;
// }

class Websocket {
    static isInit = false;
    static protocol = 'ws:';
    static baseUrl = Api.baseUrl;
    static playerId;

    static socket;
    static async init(playerId) {
        if (!this.isInit) {
            this.isInit = true;
            window.mywebsoc = this;
        }
        else return;

        return new Promise((resolve => {
            this.playerId = playerId;
            this.socket = new WebSocket(
                this.protocol + this.baseUrl
                + '?token=' + JSON.parse(window.sessionStorage.getItem('_user')).token
            );

            this.socket.onmessage = (message) => {
                console.log(message.data);
                debugger
            };

            this.socket.onopen = () => {
                resolve()
            };
        }));
    }

    static subscribe(lobbyId) {
        Websocket.socket.send(
            JSON.stringify({
                from: this.playerId,
                type: 'chat',
                lobbyId,
                body: {
                    to: [],
                    type: 'join',
                    body: ''
                }
            })
        )
    }
}

export default Websocket;