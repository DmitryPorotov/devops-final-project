import WebsocketWithUserInterface from "./websocket-with-user.interface";


export interface LobbyClients {
    id: number;
    owner: number;
    participants: Array<number>;
    clients: Array<WebsocketWithUserInterface>;
}
