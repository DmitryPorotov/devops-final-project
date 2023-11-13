import {ChatMessageInterface} from "./chat-message.interface";

export interface MessageInterface {
    userId?: number;
    action?: string;
    joinAs?: string;
    messageId: string;
    name?: string;
    type: 'chat' | 'action' | 'test' | 'error';
    lobbyId?: number;
    body: ChatMessageInterface;
}
