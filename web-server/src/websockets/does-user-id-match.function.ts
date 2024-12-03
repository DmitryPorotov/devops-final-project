import { MessageInterface } from './messages/message.interface';
import WebsocketWithUserInterface from './websocket-with-user.interface';
import { Logger } from '@nestjs/common';

export default function (
  client: WebsocketWithUserInterface,
  message: MessageInterface,
  logger: Logger,
): boolean {
  if (client.user.id !== message.userId) {
    logger.debug('corrupt message, messageId ' + message.messageId);
    const error: MessageInterface = {
      type: 'error',
      messageId: message.messageId,
      lobbyId: message.lobbyId,
      body: {
        type: 'error',
        body: 'message is corrupt',
      },
    };
    client.send(JSON.stringify(error));
    return false;
  }
  return true;
}
