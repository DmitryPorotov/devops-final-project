import { ConflictException, Injectable, Logger } from "@nestjs/common"
import WebsocketWithUserInterface from "./websocket-with-user.interface";
import {MessageInterface} from "./messages/message.interface";
import {Lobby as LobbyEntity} from "../lobby/entities/lobby.entity";
import {LoginUserDto} from "../user/dto/login-user.dto";
import {LobbyService} from "../lobby/lobby.service";
import { RabbitMqService } from "../rabbit-mq/rabbit-mq.service"
import { AuthToLobby } from "./auth-to-lobby.decorator"

interface Lobby {
    id: number;
    owner: number;
    participants: Array<number>;
    clients: Array<WebsocketWithUserInterface>
}

@Injectable()
class LobbyManagerService {
    private readonly logger = new Logger(LobbyManagerService.name);

    private lobbies: Map<number, Lobby> = new Map<number, Lobby>();

    private readonly instId: string;

    constructor(private lobbyService: LobbyService, protected rabbitMqService: RabbitMqService) {
        this.instId = String(Math.random()) + Math.random();
    }

    protected async init() {
        this.logger.debug('in init' + this.instId)
        await this.rabbitMqService.init(this.workerCallback, this.chatCallback)
        await this.rabbitMqService.waitForInit()
    }


    private workerCallback = (msg) => {

    }

    private chatCallback = (msg: MessageInterface) => {
        const lobby = this.lobbies.get(msg.lobbyId);
        if (msg.type === 'chat') {
            msg.name = lobby.clients.find(x => x.user.id === msg.from).user.name
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
    }

    @AuthToLobby(true)
    async create(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: LobbyEntity = null) {
        const lobby = {
            id: lobbyEntity.id,
            owner: lobbyEntity.owner.id,
            clients: [client],
            participants: lobbyEntity.participants.map(u => u.id)
        }
        this.lobbies.set(lobbyEntity.id, lobby)
        this.logger.debug(`Created: instID ${this.instId} ` + JSON.stringify({
            ...lobby,
            clients: null,
        }))
    }

    @AuthToLobby()
    async join(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: LobbyEntity = null) {
        let lobby;
        if (!this.lobbies.has(message.lobbyId)) {
            lobby = {
                id: lobbyEntity.id,
                owner: lobbyEntity.owner.id,
                clients: [client],
                participants: lobbyEntity.participants.map(u => u.id)
            }
            this.lobbies.set(lobbyEntity.id, lobby)
        } else {
            lobby = this.lobbies.get(message.lobbyId);
            lobby.clients.push(client);
            lobby.participants = lobbyEntity.participants.map(u => u.id);
        }
        this.logger.debug(`Joined: instID ${this.instId} ${client.user.id} ${JSON.stringify({
            ...lobby,
            clients: null
        })}`);
    }

    @AuthToLobby(true)
    async kick(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: LobbyEntity = null) {
        const updatedLobbyEntity = await this.lobbyService.kick(lobbyEntity.id, client.user.id, message.body.to[0]);
        const lobby = this.lobbies.get(lobbyEntity.id);
        lobby.participants = updatedLobbyEntity.participants.map(u => u.id);
        setTimeout(() => {
            lobby.clients = lobby.clients.filter(c => c.user.id !== message.body.to[0]);
        }, 5000);
    }

    @AuthToLobby()
    async leave(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: LobbyEntity = null) {
        const updatedLobbyEntity = await this.lobbyService.leave(lobbyEntity.id, client.user.id);
        if (updatedLobbyEntity.deletedAt != null) {
            setTimeout(async () => {
                this.lobbies.delete(updatedLobbyEntity.id);
                await this.rabbitMqService.unsubscribeFromChat(updatedLobbyEntity.id);
            }, 5000)
        }
        const lobby = this.lobbies.get(updatedLobbyEntity.id);
        lobby.participants = updatedLobbyEntity.participants.map(u => u.id);
        setTimeout(() => {
            lobby.clients = lobby.clients.filter(c => c.user.id !== client.user.id);
        }, 5000)
        lobby.owner = updatedLobbyEntity.owner.id;
    }

    @AuthToLobby()
    async message(client: WebsocketWithUserInterface, message: MessageInterface, lobbyEntity: LobbyEntity = null) {
        //AuthToLobby will relay the message
        return ;
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

export default LobbyManagerService;