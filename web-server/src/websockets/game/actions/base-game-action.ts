import { Logger } from '@nestjs/common';
import LobbiesClientsMapService from '../../lobbies-clients-map.service';
import WebsocketWithUserInterface from '../../websocket-with-user.interface';
import { MessageInterface } from '../../messages/message.interface';

export abstract class BaseGameAction {
  protected abstract logger: Logger;

  protected constructor(protected lobbies: LobbiesClientsMapService) {}

  public abstract doAction(
    client: WebsocketWithUserInterface,
    message: MessageInterface,
  ): Promise<void>;
}
