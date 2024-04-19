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


export class LeaveLobby extends BaseLobbyAction {

    protected readonly logger = new Logger(LeaveLobby.name);

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
        const updatedLobbyEntity = await this.lobbyService.leave(lobbyEntity.id, client.user.id);
        if (updatedLobbyEntity.deletedAt != null) {
            setTimeout(async () => {
                this.lobbies.delete(updatedLobbyEntity.id);
                await this.chatService.unsubscribeFromChat(updatedLobbyEntity.id);
            }, 5000)
        }
        const lobby = this.lobbies.get(updatedLobbyEntity.id);
        lobby.participants = updatedLobbyEntity.participants.map(u => u.id);
        setTimeout(() => {
            lobby.clients = lobby.clients.filter(c => c.user.id !== client.user.id);
        }, 5000);
        lobby.owner = updatedLobbyEntity.owner.id;
        super.relayToChat(client, message, message.body, lobbyEntity)
    }

}