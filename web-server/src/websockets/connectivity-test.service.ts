import {Injectable} from "@nestjs/common";
import {RabbitMqService} from "../rabbit-mq/rabbit-mq.service";
import { RedisService } from "../redis/redis.service";

@Injectable()
class ConnectivityTestService {
    constructor(private messagingService: RedisService) {
    }

    async sendToWorker(message, callback: (msg: string) => void ) {
        await this.messagingService.init(callback, ()=>{});
        await this.messagingService.sendToWorkersTest(message);
    }
}

export default ConnectivityTestService