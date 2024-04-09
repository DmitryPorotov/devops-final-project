import LobbiesClientsMapService from "../lobbies-clients-map.service"
import WorkerRelayService from "../../redis/worker-relay.service"
import { MessageInterface } from "../messages/message.interface"
import WebsocketWithUserInterface from "../websocket-with-user.interface"
import { Injectable, Logger } from "@nestjs/common"
import { WorkerMessageInterface } from "../messages/worker-message.interface"
import {BaseGameAction} from "./actions/base-game-action";
import {CreateGame} from "./actions/create-game";
import {RelayMessageToGame} from "./actions/relay-message-to-game";
import NoGameInRedisCacheError from "../../redis/NoGameInRedisCacheError";
import {LobbyClients} from "../lobby-clients.interface";

@Injectable()
class GameMessagingService {
    protected logger = new Logger(GameMessagingService.name);
    constructor(private lobbies: LobbiesClientsMapService, private workerRelayService: WorkerRelayService) {
    }

    protected async init() {
        await this.workerRelayService.init(this.workerCallback);
    }

    public async processMessage(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.init();
        let handler: BaseGameAction;
        if (message.action === "create_game") {
            handler = new CreateGame(this.lobbies, this.workerRelayService);
        }
        else {
            handler = new RelayMessageToGame(this.lobbies, this.workerRelayService);
        }
        try {
            await handler.doAction(client, message)
        }
        catch (e) {
            if (e instanceof NoGameInRedisCacheError && message.action === 'get_status') {
                this.workerCallback({
                    gameId: String(message.lobbyId),
                    type: "action",
                    userId: message.userId,
                    action: "get_status",
                    status: {
                        created: false,
                        details: {}
                    }
                })
            } else throw e;
        }
    }



    private workerCallback = (msg: WorkerMessageInterface) => {
        const lobby = this.lobbies.get(Number(msg.gameId));
        if (!lobby) return;
        msg.type = "action";
        if (msg.userId) {
            const client = lobby.clients.find(c => c.user.id === msg.userId);
            client?.send(JSON.stringify(msg));
            return;
        }
        if (msg.action === "game_action") {
            msg.reply.forEach(r => {
                if (r.to === '*') {
                    this.sendToAll(lobby, {
                        ...msg,
                        reply: [r]
                    })
                }
                else {
                    const client = lobby.clients.find( c => c.user.id === r.to);
                    client?.send(JSON.stringify({
                        ...msg,
                        reply: [r]
                    }));
                }
            });
        } else {
            this.sendToAll(lobby, msg)
        }
    };

    private sendToAll(lobby: LobbyClients, msg: WorkerMessageInterface) {
        lobby.clients.forEach(c => {
            c.send(JSON.stringify(msg));
        });
    }
}

export default GameMessagingService;
