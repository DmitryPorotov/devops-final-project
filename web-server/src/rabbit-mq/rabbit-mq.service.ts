import { Injectable, Logger } from '@nestjs/common'
import * as amqp from "amqplib/"
import { Channel } from "amqplib/";
import { ConsumeMessage } from "amqplib/properties";
import { Buffer } from "buffer";
import { env } from "process";
import { MessageInterface } from "../websockets/messages/message.interface"
import { MessagingProviderInterface } from "../common/messaging-provider.interface"


@Injectable()
export class RabbitMqService implements MessagingProviderInterface {
    private readonly logger = new Logger(RabbitMqService.name);
    private readonly TO_WORKERS_EXCHANGE = "to_workers";
    private readonly FROM_WORKERS_EXCHANGE = "from_workers";
    private readonly SERVER_NAME = 'server1';
    private readonly CHAT = 'chat';

    private channel: Channel = null;

    private lobbyIdToWorker: Map<string, string> = new Map<string, string>();

    private pendingNewGames: Map<number, Array<{worker: string, gamesCount: number}>> = new Map();

    private workerCallback: (Object) => void;

    private chatCallback: (Object) => void;

    private chatQueueName: string;

    private isInit = false;

    async init(workerCallback: (message: Object) => void, chatCallback: (message: Object) => void) {
        if (this.channel === null) {
            this.workerCallback = workerCallback;
            this.chatCallback = chatCallback;
            this.logger.debug('init');

            const connection = await amqp.connect(`amqp://${env.RABBIT_HOST}`);
            const channel = await connection.createChannel();
            this.channel = channel;

            await channel.assertExchange(this.TO_WORKERS_EXCHANGE, "topic", {durable: false});
            await channel.assertExchange(this.FROM_WORKERS_EXCHANGE, "topic", {durable: false});
            await channel.assertExchange(this.CHAT, "topic", {durable: false});

            const fromWorkersQueue = await channel.assertQueue('');
            const fromWorkersQueueName = fromWorkersQueue.queue;

            const chatQueue = await channel.assertQueue('');
            this.chatQueueName = chatQueue.queue;

            await channel.bindQueue(fromWorkersQueueName, this.FROM_WORKERS_EXCHANGE, `${this.SERVER_NAME}.*`);
            await channel.consume(fromWorkersQueueName, this.handleMessageFromWorker, {noAck: true});

            await channel.consume(this.chatQueueName, this.handleChatMessage, {noAck: true});

            this.logger.debug('init end');
            this.isInit = true
        }
    }

    async waitForInit() {
        return new Promise<boolean>(resolve => {
            if (this.isInit) {
                resolve(true)
            } else {
                const interval = setInterval(() => {
                    if (this.isInit) {
                        clearInterval(interval);
                        resolve(true)
                    }
                }, 2)
            }
        })
    }

    sendToWorkersTest(message) {
        this.channel.publish(this.TO_WORKERS_EXCHANGE, `worker1.${this.SERVER_NAME}`, Buffer.from(message));
    }

    async subscribeToChat(lobbyId: number) {
        await this.channel.bindQueue(this.chatQueueName, this.CHAT, `${lobbyId}.*`);
    }

    async unsubscribeFromChat(lobbyId: number) {
        await this.channel.unbindQueue(this.chatQueueName, this.CHAT, `${lobbyId}.*`);
    }

    async sendToChat(lobbyId: number, message: MessageInterface) {
        this.logger.debug('in send to chat');
        await this.subscribeToChat(lobbyId);
        this.channel.publish(this.CHAT, `${lobbyId}.${this.SERVER_NAME}`, Buffer.from(JSON.stringify(message)))
    }

    private handleChatMessage = (message: ConsumeMessage) => {
        if (!this.chatCallback) return;
        const data = JSON.parse(message.content.toString());
        this.chatCallback(data);
    };

    private handleMessageFromWorker = (message: ConsumeMessage) => {
        const workerName = message.fields.routingKey.split('.')[1];
        const data = JSON.parse(message.content.toString());
        if (data.action === 'new_game') {
            this.pendingNewGames.get(parseInt(data.gameId)).push({
                worker: workerName,
                gamesCount: data.gamesCount
            })
        } else if (data.action === 'test') {
            this.workerCallback(data);
        } else {
            this.workerCallback(data);
        }

    };

    async createNewGame(userId: number, gameId: number) {
        this.channel.publish(this.TO_WORKERS_EXCHANGE, `new_game.${this.SERVER_NAME}`, Buffer.from(JSON.stringify({
            userId,
            gameId: String(gameId),
            action: 'new_game'
        })));
        this.pendingNewGames.set(gameId, []);
        setTimeout(() => {
            const workers = this.pendingNewGames.get(gameId);
            if (workers.length) {
                const worker = workers.sort((a, b) => b.gamesCount - a.gamesCount)[0];
                this.channel.publish(this.TO_WORKERS_EXCHANGE, `${worker.worker}.${this.SERVER_NAME}`, Buffer.from(JSON.stringify({
                    action: 'create_game',
                    userId,
                    gameId: String(gameId),
                })))
            }
            else {
                this.workerCallback({
                    gameId,
                    error: "Workers are busy or are dead."
                })
            }
        }, 3000)
    }
}
