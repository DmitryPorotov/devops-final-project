import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import ChatService from "../../../redis/chat.service";
import { MessageInterface } from "../../messages/message.interface";
import { Lobby as LobbyEntity } from "src/lobby/entities/lobby.entity";
import { ChatMessageInterface } from "src/websockets/messages/chat-message.interface";
import {LoginUserDto} from "../../../user/dto/login-user.dto";
import {LobbyService} from "../../../lobby/lobby.service";
import {Logger} from "@nestjs/common";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";

export abstract class BaseLobbyAction {
    protected abstract readonly logger: Logger;

    protected constructor (protected chatService: ChatService, protected lobbyService: LobbyService, protected lobbies: LobbiesClientsMapService) {}

    abstract doAction(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity?: LobbyEntity): void

    protected async relayToChat(client: WebsocketWithUserInterface, message: MessageInterface, body: ChatMessageInterface, lobbyEntity: LobbyEntity): Promise<void> {
        await this.chatService.sendToChat(lobbyEntity.id, {
            messageId: message.messageId,
            type: 'chat',
            userId: client.user.id,
            lobbyId: message.lobbyId,
            name: client.user.name,
            time: (new Date).toISOString(),
            body
        })
    }

    protected async getLobbyIfIsParticipant(message: MessageInterface, user: LoginUserDto): Promise<LobbyEntity | null> {
        const lobby = await this.lobbyService.findOne(message.lobbyId);
        if (this.isSenderParticipantInLobby(user, lobby)) {
            return lobby;
        }
        return null;
    }

    private isSenderParticipantInLobby(user: LoginUserDto, lobby: LobbyEntity): boolean {
        return lobby?.participants.reduce((acc, cur) => {
            if (cur.id === user.id) acc = true;
            return acc;
        }, false)
    }
}