import {ChatMessageInterface} from "./chat-message.interface";

export interface MessageInterface {
    userId?: number;
    action?: string;
    joinAs?: string;
    isRandomHouses?: boolean;
    messageId: string;
    name?: string;
    time?: string;
    type: 'chat' | 'action' | 'test' | 'error';
    lobbyId?: number;
    body: ChatMessageInterface;
}
