import { Injectable, Logger } from '@nestjs/common';
import { MessageInterface } from '../websockets/messages/message.interface';
import WorkerRelayService from './worker-relay.service';
import NoGameInRedisCacheError from './NoGameInRedisCacheError';

@Injectable()
class MessageResendService {
  private readonly logger = new Logger(MessageResendService.name);

  private intervals: Map<string, number> = new Map();
  private timeouts: Map<string, number> = new Map();

  private workerRelayService: WorkerRelayService;

  init(workerRelayService: WorkerRelayService) {
    this.workerRelayService = workerRelayService;
  }

  registerMessageToResend(message: MessageInterface): void {
    this.logger.debug('in register ' + message.messageId);
    this.intervals.set(
      message.messageId,
      setInterval(async () => {
        try {
          await this.workerRelayService.sendToGame(
            message.lobbyId,
            JSON.stringify({
              ...message,
              gameId: String(message.lobbyId),
            }),
          );
        } catch (e) {
          if (
            e instanceof NoGameInRedisCacheError &&
            message.action === 'get_status'
          ) {
            this.confirmDelivery(message.messageId);
          } else throw e;
        }
      }, 2000) as unknown as number,
    );
    this.timeouts.set(
      message.messageId,
      setTimeout(() => {
        this.confirmDelivery(message.messageId, true);
      }, 10000) as unknown as number,
    );
  }

  confirmDelivery(messageId: string, timedOut = false): void {
    this.logger.debug(
      'in confirm ' + messageId + (timedOut ? ' timed out' : ''),
    );
    if (this.timeouts.has(messageId)) {
      clearTimeout(this.timeouts.get(messageId));
      this.timeouts.delete(messageId);
    }
    if (!this.intervals.has(messageId)) return;
    clearInterval(this.intervals.get(messageId));
    this.intervals.delete(messageId);
  }
}

export default MessageResendService;
