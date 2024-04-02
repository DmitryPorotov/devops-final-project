import { LobbyClients } from "./lobby-clients.interface";
import { Injectable } from "@nestjs/common"

@Injectable()
class LobbiesClientsMapService {
    private lobbies: Map<number, LobbyClients> = new Map<number, LobbyClients>();
    get(lobbyId: number): LobbyClients {
        return this.lobbies.get(lobbyId);
    }
    set(lobbyId: number, lobby: LobbyClients) {
        this.lobbies.set(lobbyId, lobby);
    }
    has(lobbyId: number): boolean {
        return this.lobbies.has(lobbyId);
    }
    delete(lobbyId: number) {
        this.lobbies.delete(lobbyId)
    }
}

export default LobbiesClientsMapService;
