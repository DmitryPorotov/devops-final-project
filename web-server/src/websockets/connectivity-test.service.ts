import {Injectable} from "@nestjs/common";
import {RabbitMqService} from "../rabbit-mq/rabbit-mq.service";

@Injectable()
class ConnectivityTestService {
    constructor(private rabbitMqService: RabbitMqService) {
    }

    async sendToWorker(message, callback: (string) => void ) {
        await this.rabbitMqService.init(callback, null);
        await this.rabbitMqService.sendToWorkersTest(message);
    }
}

export default ConnectivityTestService