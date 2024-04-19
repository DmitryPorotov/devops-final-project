import {BaseLobbyAction} from "./base-lobby-action";
import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import {MessageInterface} from "../../messages/message.interface";
import {Lobby} from "../../../lobby/entities/lobby.entity";
import ChatService from "../../../redis/chat.service";
import {AuthToLobby} from "./auth-to-lobby.decorator";
import {LobbyService} from "../../../lobby/lobby.service";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";
import {Logger} from "@nestjs/common";
import SystemMessageService from "../../system-message.service";


export class MessageLobby extends BaseLobbyAction {

    protected readonly logger = new Logger(MessageLobby.name);

    constructor(
        protected chatService: ChatService,
        protected lobbyService: LobbyService,
        protected lobbies: LobbiesClientsMapService,
        protected systemMessageService: SystemMessageService
    ) {
        super(chatService, lobbyService, lobbies, systemMessageService);
    }
    @AuthToLobby()
    async doAction(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: Lobby = null): Promise<void> {
        super.relayToChat(client, message, message.body, lobbyEntity)
    }

}