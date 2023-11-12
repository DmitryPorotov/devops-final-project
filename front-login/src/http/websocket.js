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

    /**
     * @type Map
     */
    static eventHandlers;

    /**
     * @type WebSocket
     */
    static socket;
    static async init(playerId) {
        if (!Websocket.isInit) {
            Websocket.isInit = true;
            Websocket.eventHandlers = new Map();
            Websocket.playerId = playerId;
            // window.mywebsoc = this;
        }
        else return;

        return Websocket.makeSocket();
    }

    static makeSocket() {
        return new Promise((resolve => {

            this.socket = new WebSocket(
                this.protocol + this.baseUrl
                + '?token=' + JSON.parse(window.sessionStorage.getItem('_user')).token
            );

            this.socket.addEventListener('message', (message) => {
                console.log(message.data);
            });

            const errorCb = (message) => {
                console.log(message);
                setTimeout(() => {
                    Websocket.makeSocket().then(() => {
                        Websocket.eventHandlers.forEach((cb) => {
                            Websocket.socket.addEventListener('message', cb);
                        })
                    });
                }, 1000);
            };

            this.socket.addEventListener('error', errorCb);

            this.socket.addEventListener('close', errorCb);

            this.socket.onopen = () => {
                resolve()
            };
        }));
    }

    /**
     * @typedef {{lobbyId: number,from:number, messageId: string}} Message
     */

    /**
     * @param {number} lobbyId
     * @param {function(Message)} callBack
     */
    static onMessage(lobbyId, callBack) {
        const cbWrapper = (message) => {
            /**
             * @type {Message}
             */
            const msg = JSON.parse(message.data);
            if (msg.lobbyId === lobbyId) {
                callBack(msg);
            }
        };
        this.eventHandlers.set(callBack, cbWrapper);
        Websocket.socket.addEventListener('message', cbWrapper);
    }

    static offMessage(callBack) {
        const wrapper = this.eventHandlers.get(callBack);
        if (!wrapper) return;
        this.eventHandlers.delete(callBack);
        Websocket.socket.removeEventListener('message', wrapper);
    }

    static send(message) {
        this.socket.send(JSON.stringify(message));
    }

    static subscribe(lobbyId) {
        Websocket.socket.send(
            JSON.stringify({
                userId: this.playerId,
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