import WebsocketWithUserInterface from "./websocket-with-user.interface"
import { MessageInterface } from "./messages/message.interface"
import LobbyManagerService from "./lobby-manager.service"
import { Lobby } from "../lobby/entities/lobby.entity"
import { ConflictException } from "@nestjs/common"


export function AuthToLobby(ownerOnly: boolean = false): MethodDecorator {
    return function <T2 = (this: LobbyManagerService, WebsocketWithUserInterface, MessageInterface, Lobby) => Promise<void>>
    (target: LobbyManagerService, propertyKey: string, descriptor:TypedPropertyDescriptor<T2>) {
        const original = descriptor.value as (WebsocketWithUserInterface, MessageInterface, Lobby) => Promise<void>

        descriptor.value = async function (this: LobbyManagerService, client: WebsocketWithUserInterface, message: MessageInterface, lobby: Lobby = null) {
            await this.init();
            const lobbyEntity = await this.getLobbyIfIsParticipant(message, client.user);
            if (lobbyEntity &&
                (
                    !ownerOnly
                    ||
                    (
                        ownerOnly
                        && lobbyEntity.owner.id === client.user.id
                    )
                )
            ) {
                try {
                    await original.call(this, client, message, lobbyEntity);
                    await this.rabbitMqService.sendToChat(lobbyEntity.id, {
                        messageId: message.messageId,
                        type: 'chat',
                        from: client.user.id,
                        lobbyId: message.lobbyId,
                        body: {
                            type: message.body?.type,
                            body: message.body?.body,
                            to: message.body?.to
                        }
                    })
                }
                catch (e) {
                    if (e instanceof ConflictException) {
                        const error: MessageInterface = {
                            body: {
                                type: 'error',
                                body: e.message
                            },
                            from: client.user.id,
                            lobbyId: lobbyEntity.id,
                            messageId: message.messageId,
                            type: 'error'
                        }
                        client.send(JSON.stringify(error))
                    } else throw e;
                }
            } else {
                const error: MessageInterface = {
                    type: 'error',
                    messageId: message.messageId,
                    lobbyId: message.lobbyId,
                    body: {
                        type: 'error',
                        body: ownerOnly ? 'You are not the owner of this lobby' : 'You do not participate in this lobby'
                    }
                }
                client.send(JSON.stringify(error))
            }
        } as any
    }
}
