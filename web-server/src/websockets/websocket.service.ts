import {Injectable, Logger} from "@nestjs/common";
import {MessageInterface} from "./messages/message.interface";
import {LobbyService} from "../lobby/lobby.service";
import {Lobby as LobbyEntity} from "../lobby/entities/lobby.entity";
import {LoginUserDto} from "../user/dto/login-user.dto";
import LobbyManagerService from "./lobby-manager.service";
import WebsocketWithUserInterface from "./websocket-with-user.interface";
import ConnectivityTestService from "./connectivity-test.service";
import {Buffer} from "buffer";

interface Lobby {
    id: number;
    owner: number;
    participants: Array<number>;
    clients: Array<WebsocketWithUserInterface>
}

@Injectable()
class WebsocketService {
    private logger = new Logger(WebsocketService.name)

    constructor(private lobbyService: LobbyService,
                private lobbyManagerService: LobbyManagerService,
                private connectivityTestService: ConnectivityTestService) {
    }

    // private lobbies: Map<number,Lobby> = new Map<number, Lobby>();
    //
    // private isSenderParticipantInLobby(user: LoginUserDto, lobby: LobbyEntity): boolean {
    //     return lobby.participants.reduce((acc, cur) => {
    //         if (cur.id === user.id) acc = true;
    //         return acc;
    //     }, false)
    // }

    async handleMessage(client: WebsocketWithUserInterface, message: MessageInterface) {
        if (message.type === "chat") {
            switch (message.body.type) {
                case "create": {
                    this.logger.debug('in create')
                    await this.lobbyManagerService.create(client, message);
                    break;
                }
                case "join": {
                    await this.lobbyManagerService.join(client, message);
                    break;
                }
                case "leave": {
                    await this.lobbyManagerService.leave(client, message);
                    break;
                }
                case "kick": {
                    await this.lobbyManagerService.kick(client, message);
                    break;
                }
                case "edit":
                case "message": {
                    await this.lobbyManagerService.message(client, message);
                    break;
                }
            }
        } else if (message.type === 'test') {
            await this.connectivityTestService.sendToWorker(JSON.stringify(message), (msg) => {
                this.logger.debug("from worker: " + JSON.stringify(msg));
                client.send(Buffer.from(JSON.stringify(msg)))
            })
        } else if (message.type === 'action') {

        }

        /*if (!this.lobbies.has(message.lobbyId)) {
            const lobby = await this.lobbyService.findOne(message.lobbyId);
            if (this.isSenderParticipantInLobby(client.user, lobby)) {
                this.lobbies.set(lobby.id, {
                    id: lobby.id,
                    owner: lobby.owner.id,
                    clients: [client],
                    participants: lobby.participants.map(u => u.id)
                })
            }
            else return;
        }

        const lobby = this.lobbies.get(message.lobbyId);
        //note assume only chat for now
        for (let u of lobby.clients) {
            u.send(JSON.stringify({
                lobbyId: lobby.id,
                from: message.from,
                type: "chat",
                body: {
                    to: [],
                    body: "Hello"
                }
            } as MessageInterface))
        }*/
    }


}

export default WebsocketService;