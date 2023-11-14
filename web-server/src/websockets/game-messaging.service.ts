import LobbiesClientsMapService from "./lobbies-clients-map.service"
import WorkerRelayService from "../redis/worker-relay.service"
import { MessageInterface } from "./messages/message.interface"
import WebsocketWithUserInterface from "./websocket-with-user.interface"
import { Injectable, Logger } from "@nestjs/common"
import AuthToGame from "./auth-to-game.decorator"
import { WorkerMessageInterface } from "./messages/worker-message.interface"

@Injectable()
class GameMessagingService {
    protected logger = new Logger(GameMessagingService.name)
    constructor(protected lobbies: LobbiesClientsMapService, protected workerRelayService: WorkerRelayService) {
    }

    protected async init() {
        await this.workerRelayService.init(this.workerCallback);
    }

    @AuthToGame(true)
    async create(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.workerRelayService.subscribeToGame(message.lobbyId);
        await this.workerRelayService.createNewGame(client.user.id, message.lobbyId, message.isRandomHouses, message.messageId)
    }

    @AuthToGame()
    async join(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.workerRelayService.subscribeToGame(message.lobbyId);
        await this.workerRelayService.sendToGame(message.lobbyId, JSON.stringify({
            ...message,
            gameId: String(message.lobbyId)
        }))
    }

    @AuthToGame()
    async start(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.workerRelayService.subscribeToGame(message.lobbyId);
        await this.workerRelayService.sendToGame(message.lobbyId, JSON.stringify({
            ...message,
            gameId: String(message.lobbyId)
        }))
    }

    private workerCallback = (msg: WorkerMessageInterface) => {
        //todo send to users
        const lobby = this.lobbies.get(Number(msg.gameId));
        if (!lobby) return;
        lobby.clients.forEach(c => {
            c.send(JSON.stringify(msg));
        })
    }
}

export default GameMessagingService;