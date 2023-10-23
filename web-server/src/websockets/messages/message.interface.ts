import {ChatMessageInterface} from "./chat-message.interface";

export interface MessageInterface {
    from?: number;
    messageId: string;
    type: 'chat' | 'action' | 'test' | 'error';
    lobbyId?: number;
    body: ChatMessageInterface;
}