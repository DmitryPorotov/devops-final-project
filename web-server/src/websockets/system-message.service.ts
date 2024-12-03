import { Injectable, Logger } from '@nestjs/common';
import { MessageInterface } from './messages/message.interface';
import { LobbyService } from '../lobby/lobby.service';
import WebsocketWithUserInterface from './websocket-with-user.interface';
import RedisStreamListener from '../redis/redis.stream-listener';
import constants from '../constants';
import LobbiesClientsMapService from './lobbies-clients-map.service';

@Injectable()
class SystemMessageService {
  private readonly logger = new Logger(SystemMessageService.name);

  private readonly chats: Map<number, (msg: string) => void> = new Map<
    number,
    (msg: string) => void
  >();

  constructor(
    private readonly lobbyService: LobbyService,
    private readonly lobbiesClientsMapService: LobbiesClientsMapService,
    private readonly redisStreamListener: RedisStreamListener,
  ) {}

  async relayToLobbies(
    client: WebsocketWithUserInterface,
    message: MessageInterface,
  ) {
    await this.redisStreamListener.init();
    const lobbyIds = await this.lobbyService.findLobbyIdsByParticipantId(
      client.user.id,
    );
    for (const lobbyId of lobbyIds) {
      message.lobbyId = lobbyId;
      await this.redisStreamListener.send(
        `${constants.CHAT_PREFIX}${lobbyId}sys`,
        JSON.stringify(message),
      );
    }
  }

  private callback(data: MessageInterface) {
    this.lobbiesClientsMapService.has(data.lobbyId) &&
      this.lobbiesClientsMapService
        .get(data.lobbyId)
        .clients.forEach((client) => client.send(JSON.stringify(data)));
  }

  async subscribeToSystemEvents(lobbyId: number) {
    if (this.chats.has(lobbyId)) return;
    const chatCb = (msg: string) => {
      const data = JSON.parse(msg);
      this.callback(data);
    };
    this.chats.set(lobbyId, chatCb);
    const id = String(new Date().getTime()) + '-0';
    await this.redisStreamListener.addListener(
      `${constants.CHAT_PREFIX}${lobbyId}sys`,
      id,
      chatCb,
    );
  }

  async unsubscribeFromSystemEvents(lobbyId: number) {
    if (!this.chats.has(lobbyId)) return;
    await this.redisStreamListener.removeListener(
      `${constants.CHAT_PREFIX}${lobbyId}sys`,
      this.chats.get(lobbyId),
    );
  }
}

export default SystemMessageService;
