import { createClient } from "redis";
import { env } from "process";
import { Logger } from "@nestjs/common";

class RedisPubSub {
    private readonly logger = new Logger(RedisPubSub.name);
    // private readonly TO_WORKERS = "to_workers";
    // private readonly FROM_WORKERS = "from_workers";
    private readonly SERVER_NAME = 'server1';//todo get from env

    private redisPublisher;
    private redisSubscriber;

    private workerCallback: (msg: string) => void;

    private onNewGameMessage: (gameId: string, workerName: string, gamesCount: number) => void;

    async init(callback: (msg: Object) => void, onNewGameMessage: (gameId: string, workerName: string, gamesCount: number) => void) {
        this.workerCallback = callback;
        this.onNewGameMessage = onNewGameMessage;
        this.redisPublisher = createClient({url: env.REDIS_URL});
        this.redisPublisher.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisPublisher.connect();
        this.redisSubscriber = createClient({url: env.REDIS_URL});
        this.redisSubscriber.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisSubscriber.connect();
        await this.redisSubscriber.pSubscribe(`${this.SERVER_NAME}.*`, this.onMessageFromWorker);
        this.logger.debug('pub sub init end')
    }

    private onMessageFromWorker = (message: string, channel: string) => {
        const data = JSON.parse(message);
        const workerName = channel.split('.')[1];
        if (data.action === 'new_game') {
            this.onNewGameMessage(data.gameId, workerName, data.gamesCount)
        } else {
            this.workerCallback(data);
        }
    };

    sendToWorkerTest(message: string) {
        this.redisPublisher.publish(`worker1.${this.SERVER_NAME}`, message);
    }

    publishToWorker(toChannel: string, message: string) {
        this.publishToGame(toChannel, this.SERVER_NAME, message);
    }

    publishToGame(toChannel: string, returnTo: string, message: string) {
        this.redisPublisher.publish(`${toChannel}.${returnTo}`, message);
    }

    async subscribe(pattern: string, handler: (message: string, channel: string) => void) {
        await this.redisSubscriber.pSubscribe(pattern, handler);
    }

    async unsubscribe(pattern: string) {
        await this.redisSubscriber.pUnsubscribe(pattern);
    }

}

export default RedisPubSub;