import {BaseGameAction} from "./base-game-action";
import {Logger} from "@nestjs/common";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";
import WorkerRelayService from "../../../redis/worker-relay.service";
import AuthToGame from "./auth-to-game.decorator";
import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import {MessageInterface} from "../../messages/message.interface";

export class FillWithBots extends BaseGameAction {
    protected logger: Logger = new Logger(FillWithBots.name);

    constructor(protected lobbies: LobbiesClientsMapService, protected workerRelayService: WorkerRelayService) {
        super(lobbies);
    }

    @AuthToGame(true)
    public async doAction(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.workerRelayService.sendToBot(message.lobbyId, JSON.stringify(message))
    }

}