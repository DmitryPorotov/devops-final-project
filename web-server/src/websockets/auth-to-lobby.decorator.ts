import WebsocketWithUserInterface from "./websocket-with-user.interface"
import { MessageInterface } from "./messages/message.interface"
import LobbyManagerService from "./lobby-manager.service"
import { Lobby } from "../lobby/entities/lobby.entity"
import { ConflictException } from "@nestjs/common"
import { ChatMessageInterface } from "./messages/chat-message.interface"


export function AuthToLobby(ownerOnly: boolean = false): MethodDecorator {
    return function <T2 = (this: LobbyManagerService, WebsocketWithUserInterface, MessageInterface, Lobby) => Promise<void>>
    (target: LobbyManagerService, propertyKey: string, descriptor:TypedPropertyDescriptor<T2>) {
        const original = descriptor.value as (WebsocketWithUserInterface, MessageInterface, Lobby) => Promise<void>;

        descriptor.value = async function (this: LobbyManagerService, client: WebsocketWithUserInterface, message: MessageInterface, lobby: Lobby = null) {
            try {
                await this.init();
                const lobbyEntity = await this.getLobbyIfIsParticipant(message, client.user);
                this.logger.debug('has lobbyEntity', lobbyEntity != null);
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
                        let lobby;
                        if (!this.lobbies.has(message.lobbyId)) {
                            lobby = {
                                id: lobbyEntity.id,
                                owner: lobbyEntity.owner.id,
                                clients: [client],
                                participants: lobbyEntity.participants.map(u => u.id)
                            };
                            this.lobbies.set(lobbyEntity.id, lobby)
                        } else {
                            lobby = this.lobbies.get(message.lobbyId);
                            if (!lobby.clients.includes(client)) {
                                lobby.clients.push(client);
                            }
                            lobby.participants = lobbyEntity.participants.map(u => u.id);
                        }
                        this.logger.debug('trying to process in decorator');
                        await original.call(this, client, message, lobbyEntity);
                        const body: ChatMessageInterface = {
                            type: message.body?.type,
                            body: message.body?.body,
                            to: message.body?.to
                        };
                        if (message.body.deletePassword != null) {
                            body.deletePassword = message.body.deletePassword;
                        }
                        if (message.body.lobbyName) {
                            body.lobbyName = message.body.lobbyName;
                        }
                        await this.messagingService.sendToChat(lobbyEntity.id, {
                            messageId: message.messageId,
                            type: 'chat',
                            userId: client.user.id,
                            lobbyId: message.lobbyId,
                            body
                        })
                    } catch (e) {
                        if (e instanceof ConflictException) {
                            const error: MessageInterface = {
                                body: {
                                    type: 'error',
                                    body: e.message
                                },
                                userId: client.user.id,
                                lobbyId: lobbyEntity.id,
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
