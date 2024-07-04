import Api from "./api";
import Storage_ from "./storage";

class Websocket {
    static isInit: boolean = false;
    static protocol = window.envVars.protocol.endsWith('s') ? 'wss:' : 'ws:';
    static baseUrl = Api.baseUrl;
    static port = ':' + window.envVars.wsPort;
    static path = '/ws';
    static playerId: number;
    static worker: SharedWorker;
    /**
     * @type Map
     */
    static eventHandlers: Map<(msg:object)=>Promise<void>, (msg:object)=>void>;

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
        return new Promise<void>(async resolve => {
            Websocket.worker = new SharedWorker('/fwc/worker/worker.js');
            Websocket.worker.addEventListener('error',(e) =>
                console.log(e)
            );
            // Websocket.worker.onmessageerror = (e) => {
            //     console.log(e)
            // };
            const user = Storage_.getUser();
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

    static onMessage(lobbyId: number, callBack: (message: object) => Promise<void>) {
        if (Websocket.eventHandlers.has(callBack)) return;
        const cbWrapper = (msg) => {
            // eslint-disable-next-line
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
