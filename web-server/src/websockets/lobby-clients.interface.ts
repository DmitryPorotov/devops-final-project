import WebsocketWithUserInterface from './websocket-with-user.interface';

export interface LobbyClients {
  owner: number;
  participants: Array<number>;
  clients: Array<WebsocketWithUserInterface>;
}
