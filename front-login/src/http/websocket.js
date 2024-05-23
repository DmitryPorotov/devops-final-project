import Api from "./api";
import Storage from "./storage";

// export interface MessageInterface {
//     from: number;
//     type: 'chat' | 'action';
//     lobbyId: number;
//     body: ChatMessageInterface;
// }

class Websocket {
    static isInit = false;
    static protocol = window.envVars.protocol.endsWith('s') ? 'wss:' : 'ws:';
    static baseUrl = Api.baseUrl;
    static port = window.envVars.wsPort;
    static path = '/ws';
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
        }
        else return;

        return Websocket.makeSocket();
    }

    static makeSocket() {
        return new Promise(async resolve => {
            Websocket.worker = new SharedWorker('/worker/worker.js');
            Websocket.worker.onerror = (e) => console.log(e);
            Websocket.worker.onmessageerror = (e) => {
                console.log(e)
            };
            const user = await Storage.getUser();
            Websocket.worker.port.postMessage({
                action: 'init',
                args: [
                    Websocket.protocol + Websocket.baseUrl + Websocket.port + Websocket.path,
                    Websocket.playerId,
                    user.token
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
        });
    }

    /**
     * @typedef {{lobbyId: number,from:number, messageId: string}} Message
     */

    /**
     * @callback OnMessageCallBack
     * @param message
     * @return Promise
     */

    /**
     * @param {number} lobbyId
     * @param {OnMessageCallBack} callBack
     */
    static onMessage(lobbyId, callBack) {
        if (Websocket.eventHandlers.has(callBack)) return;
        const cbWrapper = (msg) => {
            if (msg.data.lobbyId === lobbyId || msg.data.gameId == lobbyId) {
                callBack(msg.data).then();
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
    }
}

export default Websocket;