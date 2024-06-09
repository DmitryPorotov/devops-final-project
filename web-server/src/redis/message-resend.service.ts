import {Injectable, Logger} from "@nestjs/common";
import {MessageInterface} from "../websockets/messages/message.interface";
import WorkerRelayService from "./worker-relay.service";

@Injectable()
class MessageResendService {
    private readonly logger = new Logger(MessageResendService.name);

    private intervals: Map<string, number> = new Map();

    private workerRelayService: WorkerRelayService;

    init(workerRelayService: WorkerRelayService) {
        this.workerRelayService = workerRelayService;
    }

    registerMessageToResend(message: MessageInterface): void {
        this.logger.debug('in register ' + message.messageId);
        this.intervals.set(message.messageId, setInterval(() => {
                this.workerRelayService.sendToGame(message.lobbyId, JSON.stringify({
                    ...message,
                    gameId: String(message.lobbyId)
                })).then()
            }, 2000) as unknown as number
        );
        setTimeout(() => {
            this.confirmDelivery(message.messageId)
        }, 10000)
    }

    confirmDelivery(messageId: string): void {
        this.logger.debug('in confirm ' + messageId);
        if (!this.intervals.has(messageId)) return;
        const interval = this.intervals.get(messageId);
        clearInterval(interval);
        this.intervals.delete(messageId);
    }
}

export default MessageResendService;