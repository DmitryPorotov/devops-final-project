import { MessageInterface } from "../websockets/messages/message.interface";

export interface MessagingProviderInterface {
    init(workerCallback: (message: Object) => void, chatCallback: (message: Object) => void): Promise<void>;

    waitForInit(): Promise<boolean>;

    // sendToWorkersTest(message): void;

    subscribeToChat(lobbyId: number): Promise<void>;

    unsubscribeFromChat(lobbyId: number): Promise<void>;

    sendToChat(lobbyId: number, message: MessageInterface): Promise<void>;
}
