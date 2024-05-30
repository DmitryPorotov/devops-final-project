import RedisStreamListener from "../redis/redis.stream-listener";
import {Injectable, Logger} from '@nestjs/common';
import constants from "../constants";
import {Observable,Subscriber} from "rxjs";
import {MessageInterface} from "../websockets/messages/message.interface";

@Injectable()
class RedisChatService {
    private readonly logger = new Logger(RedisChatService.name);

    constructor(private redisStreamListener: RedisStreamListener) {
    }

    async init() {
        return this.redisStreamListener.init();
    }

    async getLobbyChatMessagesObservable(lobbyId: number): Promise<Observable<MessageInterface>> {
        const messageId = String(new Date().getTime()) + "-0";
        let subObj: Partial<Subscriber<MessageInterface>> = null;
        const chatCb = function(message) {
            if (subObj) subObj.next(message)
        };
        await this.redisStreamListener.addListener(`${constants.CHAT_PREFIX}${lobbyId}`, messageId, chatCb);
        return new Observable<MessageInterface>((subscriber) => {
            subObj = subscriber
        })
    }

}

export default RedisChatService;