import { Injectable, Logger } from "@nestjs/common"
import WebsocketWithUserInterface from "../websocket-with-user.interface";
import {MessageInterface} from "../messages/message.interface";
import {LobbyService} from "../../lobby/lobby.service";
import ChatService from "../../redis/chat.service";
import LobbiesClientsMapService from "../lobbies-clients-map.service"
import {CreateLobby} from "./actions/create-lobby";
import {BaseLobbyAction} from "./actions/base-lobby-action";
import {JoinLobby} from "./actions/join-lobby";
import {LeaveLobby} from "./actions/leave-lobby";
import {KickFromLobby} from "./actions/kick-from-lobby";
import {EditLobby} from "./actions/edit-lobby";
import {MessageLobby} from "./actions/message-lobby";
import SystemMessageService from "../system-message.service";

@Injectable()
class LobbyManagerService {
    protected readonly logger = new Logger(LobbyManagerService.name);

    private readonly instId: string;

    constructor(
        private lobbyService: LobbyService,
        private messagingService: ChatService,
        private lobbies: LobbiesClientsMapService,
        private systemMessageService: SystemMessageService
    ) {
        this.instId = String(Math.random()) + Math.random();
    }

    protected async init() {
        this.logger.debug('in init ' + this.instId);
        await this.messagingService.init(this.chatCallback);
        await this.messagingService.waitForInit();
    }

    private chatCallback = (msg: MessageInterface) => {
        this.logger.debug('chat callback');
        const lobby = this.lobbies.get(msg.lobbyId);
        if (!lobby) {
            this.logger.warn('No lobby ' + msg.lobbyId);
            return;
        }
        lobby.clients.forEach(c => {
                if (
                    (
                        msg.type === 'chat'
                        && msg.body?.type === 'message'
                        && (!msg.body?.to?.length || msg.body.to.includes(c.user.id))
                    )
                    ||
                    (
                        msg.type !== 'chat'
                        || !msg.body
                        || msg.body.type !== 'message'
                    )
                ) {
                    c.send(JSON.stringify(msg));
                }
            }
        )
    };

    public async processMessage(client: WebsocketWithUserInterface, message: MessageInterface) {
        await this.init();
        let handler: BaseLobbyAction;
        switch (message.body.type) {
            case "create": {
                handler = new CreateLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            case "join": {
                handler = new JoinLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            case "leave": {
                handler = new LeaveLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            case "kick": {
                handler = new KickFromLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            case "edit": {
                handler = new EditLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            case "message": {
                handler = new MessageLobby(this.messagingService, this.lobbyService, this.lobbies, this.systemMessageService);
                break;
            }
            default: throw new Error(`Unknown chat message type '${message.body.type}'.`);
        }
        handler.doAction(client, message)
    }

}

export default LobbyManagerService;
