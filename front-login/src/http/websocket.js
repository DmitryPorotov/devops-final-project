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
    static worker;
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
            Websocket.worker = new SharedWorker('/worker/worker.js');
            Websocket.onerror = (e) => console.log(e);
            Websocket.worker.onmessageerror = (e) => {
                console.log(e)
            };
            Websocket.worker.port.postMessage({
                action: 'init',
                args: [
                    Websocket.playerId,
                    JSON.parse(window.sessionStorage.getItem('_user')).token
                ]
            });
            const opened = (message) => {
               if (message.data === 'opened') {
                   Websocket.worker.port.removeEventListener('message', opened);
                   resolve();
               }
            } ;
            Websocket.worker.port.addEventListener('message', opened);
            Websocket.worker.port.start();
            // this.socket = new WebSocket(
            //     this.protocol + this.baseUrl
            //     + '?token=' + JSON.parse(window.sessionStorage.getItem('_user')).token
            // );
            //
            // this.socket.addEventListener('message', (message) => {
            //     console.log(message.data);
            // });
            //
            // const errorCb = (message) => {
            //     console.log(message);
            //     setTimeout(() => {
            //         Websocket.makeSocket().then(() => {
            //             Websocket.eventHandlers.forEach((cb) => {
            //                 Websocket.socket.addEventListener('message', cb);
            //             })
            //         });
            //     }, 1000);
            // };
            //
            // this.socket.addEventListener('error', errorCb);
            //
            // this.socket.addEventListener('close', errorCb);
            //
            // this.socket.onopen = () => {
            //     resolve()
            // };
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
        const cbWrapper = (msg) => {
            /**
             * @type {Message} msg
             */
            // const msg = JSON.parse(message.data);
            if (msg.lobbyId === lobbyId) {
                callBack(msg);
            }
        };
        Websocket.eventHandlers.set(callBack, cbWrapper);
        Websocket.worker.port.addEventListener('message', cbWrapper);
    }

    static offMessage(callBack) {
        const wrapper = Websocket.eventHandlers.get(callBack);
        if (!wrapper) return;
        Websocket.eventHandlers.delete(callBack);
        Websocket.worker.port.removeEventListener('message', wrapper);
    }

    static send(message) {
        this.worker.port.postMessage({
            action: 'send',
            args: [message]
        });
    }

    static subscribe(lobbyId) {
        Websocket.worker.port.postMessage({
            action: 'setLobbyId',
            args: [lobbyId]
        });
        Websocket.worker.port.postMessage({
            action: 'subscribe',
        })
        // Websocket.socket.send(
        //     JSON.stringify({
        //         userId: this.playerId,
        //         type: 'chat',
        //         lobbyId,
        //         body: {
        //             to: [],
        //             type: 'join',
        //             body: ''
        //         }
        //     })
        // )
    }
}

export default Websocket;