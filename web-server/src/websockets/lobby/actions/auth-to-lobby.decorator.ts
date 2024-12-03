import WebsocketWithUserInterface from '../../websocket-with-user.interface';
import { MessageInterface } from '../../messages/message.interface';
import { ConflictException } from '@nestjs/common';
import { BaseLobbyAction } from './base-lobby-action';
import doesUserIdMatch from '../../does-user-id-match.function';

export default function AuthToLobby(
  ownerOnly: boolean = false,
): MethodDecorator {
  return function <
    T2 = (
      this: BaseLobbyAction,
      client: WebsocketWithUserInterface,
      message: MessageInterface,
    ) => Promise<void>,
  >(
    target: BaseLobbyAction,
    propertyKey: string,
    descriptor: TypedPropertyDescriptor<T2>,
  ) {
    const original = descriptor.value as (
      WebsocketWithUserInterface,
      MessageInterface,
      Lobby,
    ) => Promise<void>;

    descriptor.value = async function (
      this: BaseLobbyAction,
      client: WebsocketWithUserInterface,
      message: MessageInterface,
    ) {
      try {
        const lobbyEntity = await this.getLobbyIfIsParticipant(
          message,
          client.user,
        );
        this.logger.debug('has lobbyEntity', lobbyEntity != null);
        if (!doesUserIdMatch(client, message, this.logger)) {
          return;
        }
        if (
          lobbyEntity &&
          (!ownerOnly || (ownerOnly && lobbyEntity.owner.id === client.user.id))
        ) {
          try {
            let lobby;
            if (!this.lobbies.has(message.lobbyId)) {
              lobby = {
                owner: lobbyEntity.owner.id,
                clients: [client],
                participants: lobbyEntity.participants.map((u) => u.id),
              };
              this.lobbies.set(lobbyEntity.id, lobby);
            } else {
              lobby = this.lobbies.get(message.lobbyId);
              const clientIdx = lobby.clients.findIndex(
                (c) => c.user.id === client.user.id,
              );
              if (clientIdx >= 0) {
                lobby.clients[clientIdx] = client;
              } else {
                lobby.clients.push(client);
              }
              lobby.owner = lobbyEntity.owner.id;
              lobby.participants = lobbyEntity.participants.map((u) => u.id);
            }
            this.logger.debug('trying to process in decorator');
            await original.call(this, client, message, lobbyEntity);
          } catch (e) {
            if (e instanceof ConflictException) {
              const error: MessageInterface = {
                body: {
                  type: 'error',
                  body: e.message,
                },
                userId: client.user.id,
                lobbyId: lobbyEntity.id,
                messageId: message.messageId,
                type: 'error',
              };
              client.send(JSON.stringify(error));
            } else throw e;
          }
        } else {
          this.logger.debug('in decorator else');
          const error: MessageInterface = {
            type: 'error',
            messageId: message.messageId,
            lobbyId: message.lobbyId,
            body: {
              type: 'error',
              body: ownerOnly
                ? 'You are not the owner of this lobby'
                : 'You do not participate in this lobby',
            },
          };
          client.send(JSON.stringify(error));
        }
      } catch (e) {
        this.logger.warn('decorator exception ' + e, e.stack);
        throw e;
      }
    } as any;
  };
}
