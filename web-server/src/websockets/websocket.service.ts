import { Injectable, Logger } from '@nestjs/common';
import { MessageInterface } from './messages/message.interface';
import LobbyManagerService from './lobby/lobby-manager.service';
import WebsocketWithUserInterface from './websocket-with-user.interface';
import ConnectivityTestService from './connectivity-test.service';
import GameMessagingService from './game/game-messaging.service';
import { env } from 'process';

@Injectable()
class WebsocketService {
  private readonly logger = new Logger(WebsocketService.name);
  private readonly allowClientSideMessageId: boolean;

  constructor(
    private lobbyManagerService: LobbyManagerService,
    private connectivityTestService: ConnectivityTestService,
    private gameMessagingService: GameMessagingService,
  ) {
    this.allowClientSideMessageId = env.CLIENT_SIDE_MESSAGE_ID === 'true';
  }

  async handleMessage(
    client: WebsocketWithUserInterface,
    message: MessageInterface,
  ) {
    if (!message.userId) {
      const error: MessageInterface = {
        userId: client.user?.id,
        type: 'error',
        messageId: message.messageId,
        body: {
          type: 'error',
          body: "No 'userId' in message.",
        },
      };
      client.send(JSON.stringify(error));
      return;
    }
    if (!this.allowClientSideMessageId) {
      delete message.messageId;
    }
    if (message.type === 'chat') {
      await this.lobbyManagerService.processMessage(client, message);
    } else if (message.type === 'action') {
      await this.gameMessagingService.processMessage(client, message);
    } else if (message.type === 'test') {
      await this.connectivityTestService.sendToWorker(
        JSON.stringify(message),
        (msg) => {
          this.logger.debug('from worker: ' + JSON.stringify(msg));
          client.send(JSON.stringify(msg));
        },
      );
    } else {
      this.logger.warn(`Unknown message.type "${message.type}"`);
    }
  }
}

export default WebsocketService;
