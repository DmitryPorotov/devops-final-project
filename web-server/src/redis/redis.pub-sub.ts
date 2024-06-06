import { createClient } from "redis";
import { env } from "process";
import { Logger } from "@nestjs/common";

class RedisPubSub {
    private readonly logger = new Logger(RedisPubSub.name);
    private readonly SERVER_NAME = env.SERVER_NAME;

    private redisPublisher;
    private redisSubscriber;

    private onNewGameMessage: (gameId: string, workerName: string, gamesCount: number) => void;

    private onShutdownMessage: (workerName: string) => void;

    async init(
        onNewGameMessage: (gameId: string, workerName: string, gamesCount: number) => void,
        onShutdownMessage: (workerName: string) => void
    ) {
        this.onNewGameMessage = onNewGameMessage;
        this.onShutdownMessage = onShutdownMessage;
        this.redisPublisher = createClient({url: env.REDIS_URL});
        this.redisPublisher.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisPublisher.connect();
        this.redisSubscriber = createClient({url: env.REDIS_URL});
        this.redisSubscriber.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisSubscriber.connect();
        await this.redisSubscriber.pSubscribe([`${this.SERVER_NAME}.*`, 'servers.*'], this.onSystemMessageFromWorker);
        this.logger.debug('pub sub init end')
    }

    private onSystemMessageFromWorker = (message: string, channel: string) => {
        const data = JSON.parse(message);
        const workerName = channel.split('.')[1];
        if (data.action === 'new_game') {
            this.onNewGameMessage(data.gameId, workerName, data.gamesCount)
        }
        else if (data.action === 'shutdown') {
            this.onShutdownMessage(workerName);
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