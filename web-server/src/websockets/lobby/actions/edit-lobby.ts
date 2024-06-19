import {BaseLobbyAction} from "./base-lobby-action";
import WebsocketWithUserInterface from "../../websocket-with-user.interface";
import {MessageInterface} from "../../messages/message.interface";
import {Lobby} from "../../../lobby/entities/lobby.entity";
import ChatService from "../../../redis/chat.service";
import AuthToLobby from "./auth-to-lobby.decorator";
import {ChatMessageInterface} from "../../messages/chat-message.interface";
import {LobbyService} from "../../../lobby/lobby.service";
import LobbiesClientsMapService from "../../lobbies-clients-map.service";
import {Logger} from "@nestjs/common";
import SystemMessageService from "../../system-message.service";


export class EditLobby extends BaseLobbyAction {

    protected readonly logger = new Logger(EditLobby.name);

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
        const body: ChatMessageInterface = {
            type: message.body?.type,
            body: message.body?.body,
            to: message.body?.to
        };
        if (message.body.deletePassword != null) {
            body.deletePassword = message.body.deletePassword;
        }
        if (message.body.lobbyName) {
            body.lobbyName = message.body.lobbyName;
        }
        //note: the actual editing is done through http api
        super.relayToChat(client, message, body, lobbyEntity)
    }

}