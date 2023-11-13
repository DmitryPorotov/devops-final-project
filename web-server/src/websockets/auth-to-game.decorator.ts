import WebsocketWithUserInterface from "./websocket-with-user.interface"
import { MessageInterface } from "./messages/message.interface"
import { ConflictException } from "@nestjs/common"
import GameMessagingService from "./game-messaging.service"

export function AuthToGame(ownerOnly: boolean = false): MethodDecorator {
    return function <T2 = (this: GameMessagingService, client: WebsocketWithUserInterface, message: MessageInterface) => Promise<void>>
    (target: GameMessagingService, propertyKey: string, descriptor:TypedPropertyDescriptor<T2>) {
        const original = descriptor.value as (WebsocketWithUserInterface, MessageInterface) => Promise<void>;

        descriptor.value = async function (this: GameMessagingService, client: WebsocketWithUserInterface, message: MessageInterface) {
            try {
                await this.init();
                const lobby = this.lobbies.get(message.lobbyId)
                if (client.user.id !== message.userId) {
                    this.logger.debug('corrupt message, messageId ' + message.messageId);
                    const error: MessageInterface = {
                        type: 'error',
                        messageId: message.messageId,
                        lobbyId: message.lobbyId,
                        body: {
                            type: 'error',
                            body: 'message is corrupt'
                        }
                    };
                    client.send(JSON.stringify(error));
                    return
                }
                if (lobby &&
                    lobby.participants.includes(client.user.id) &&
                    (
                        !ownerOnly
                        ||
                        (
                            ownerOnly
                            && lobby.owner === client.user.id
                        )
                    )
                ) {
                    try {
                        await original.call(this, client, message);
                    } catch (e) {
                        if (e instanceof ConflictException) {
                            const error: MessageInterface = {
                                body: {
                                    type: 'error',
                                    body: e.message
                                },
                                userId: client.user.id,
                                lobbyId: lobby.id,
                                messageId: message.messageId,
                                type: 'error'
                            };
                            client.send(JSON.stringify(error))
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
                            body: ownerOnly ? 'You are not the owner of this lobby' : 'You do not participate in this lobby'
                        }
                    };
                    client.send(JSON.stringify(error))
                }
            } catch (e) {
                this.logger.warn('decorator exception ' + e)
            }
        } as any
    }
}


export default AuthToGame;