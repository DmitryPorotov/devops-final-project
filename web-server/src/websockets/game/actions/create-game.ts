import { BaseGameAction } from './base-game-action';
import WebsocketWithUserInterface from '../../websocket-with-user.interface';
import { MessageInterface } from '../../messages/message.interface';
import { Logger } from '@nestjs/common';
import LobbiesClientsMapService from '../../lobbies-clients-map.service';
import WorkerRelayService from '../../../redis/worker-relay.service';
import AuthToGame from './auth-to-game.decorator';

export class CreateGame extends BaseGameAction {
  protected logger: Logger = new Logger(CreateGame.name);

  constructor(
    protected lobbies: LobbiesClientsMapService,
    protected workerRelayService: WorkerRelayService,
  ) {
    super(lobbies);
  }

  @AuthToGame(true)
  public async doAction(
    client: WebsocketWithUserInterface,
    message: MessageInterface,
  ) {
    await this.workerRelayService.subscribeToGame(message.lobbyId);
    await this.workerRelayService.createNewGame(
      client.user.id,
      message.lobbyId,
      message.isRandomHouses,
      message.messageId,
    );
  }
}
