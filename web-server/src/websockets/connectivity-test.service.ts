import {Injectable} from "@nestjs/common";
import WorkerRelayService from "../redis/worker-relay.service";

@Injectable()
class ConnectivityTestService {
    private messagingService;
    constructor(
    ) {
        this.messagingService = new WorkerRelayService()
    }

    async sendToWorker(message, callback: (msg: string) => void ) {
        await this.messagingService.init(callback);
        await this.messagingService.sendToWorkersTest(message);
    }
}

export default ConnectivityTestService