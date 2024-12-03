import { Injectable } from '@nestjs/common';
import WorkerRelayService from '../redis/worker-relay.service';
import { WorkerMessageInterface } from './messages/worker-message.interface';

@Injectable()
class ConnectivityTestService {
  constructor(private messagingService: WorkerRelayService) {}

  async sendToWorker(message, callback: (msg: WorkerMessageInterface) => void) {
    await this.messagingService.init(callback);
    await this.messagingService.sendToWorkersTest(message);
  }
}

export default ConnectivityTestService;
