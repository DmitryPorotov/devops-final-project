import {ChatMessageInterface} from "./chat-message.interface";

export interface MessageInterface {
    from?: number;
    messageId: string;
    name?: string;
    type: 'chat' | 'action' | 'test' | 'error';
    lobbyId?: number;
    body: ChatMessageInterface;
}
