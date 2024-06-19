import {BaseLobbyAction} from "./base-lobby-action";
import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import {MessageInterface} from "../../messages/message.interface";
import {Lobby} from "../../../lobby/entities/lobby.entity";
import ChatService from "../../../redis/chat.service";
import AuthToLobby from "./auth-to-lobby.decorator";
import {LobbyService} from "../../../lobby/lobby.service";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";
import {Logger} from "@nestjs/common";
import SystemMessageService from "../../system-message.service";


export class KickFromLobby extends BaseLobbyAction {

    protected readonly logger = new Logger(KickFromLobby.name);

    constructor(
        protected chatService: ChatService,
        protected lobbyService: LobbyService,
        protected lobbies: LobbiesClientsMapService,
        protected systemMessageService: SystemMessageService
    ) {
        super(chatService, lobbyService, lobbies, systemMessageService);
    }
    @AuthToLobby(true)
    async doAction(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: Lobby = null): Promise<void> {
        const updatedLobbyEntity = await this.lobbyService.kick(lobbyEntity.id, client.user.id, message.body.to[0]);
        const lobby = this.lobbies.get(lobbyEntity.id);
        lobby.participants = updatedLobbyEntity.participants.map(u => u.id);
        setTimeout(() => {
            lobby.clients = lobby.clients.filter(c => c.user.id !== message.body.to[0]);
        }, 5000);
        super.relayToChat(client, message, message.body, lobbyEntity)
    }

}