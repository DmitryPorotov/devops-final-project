import {Observable,Subscriber} from 'rxjs';
import {Injectable, Logger} from "@nestjs/common";
import {RedisClientType} from "redis";
import {WorkerMessageInterface} from "../websockets/messages/worker-message.interface";
import RedisClientsService from "./redis.clients.service";

@Injectable()
class RedisSubscribeService {
    private readonly logger = new Logger(RedisSubscribeService.name);

    private redisSubscriber: RedisClientType;

    constructor(private redisClientsService: RedisClientsService) {
    }

    async init() {
        if (this.redisSubscriber) return;
        this.redisSubscriber = await this.redisClientsService.getNewClient();
    }

    async getLobbyMessagesFromWorkerObservable(lobbyId: number): Promise<Observable<WorkerMessageInterface>> {
        let subObj: Partial<Subscriber<WorkerMessageInterface>> = null;
        const onMessageCb = function(message) {
            if (subObj) subObj.next(JSON.parse(message))
        };
        await this.redisSubscriber.pSubscribe(`game${lobbyId}.*`, onMessageCb);
        return new Observable<WorkerMessageInterface>(subscriber => {
            subObj = subscriber
        })
    }
}

export default RedisSubscribeService;