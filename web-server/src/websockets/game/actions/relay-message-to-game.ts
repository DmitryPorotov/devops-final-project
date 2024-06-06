import {BaseGameAction} from "./base-game-action";
import {Logger} from "@nestjs/common";
import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import {MessageInterface} from "../../messages/message.interface";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";
import WorkerRelayService from "../../../redis/worker-relay.service";
import AuthToGame from "./auth-to-game.decorator";

export class RelayMessageToGame extends BaseGameAction{
    protected logger: Logger = new Logger(RelayMessageToGame.name);

    constructor(protected lobbies: LobbiesClientsMapService, protected workerRelayService: WorkerRelayService) {
        super(lobbies);
    }

    @AuthToGame()
    public async doAction(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.workerRelayService.subscribeToGame(message.lobbyId);
        try {
            await this.workerRelayService.sendToGame(message.lobbyId, JSON.stringify({
                ...message,
                gameId: String(message.lobbyId)
            }));
        }
        catch (e) {
            this.logger.error(e)
        }
    }

}