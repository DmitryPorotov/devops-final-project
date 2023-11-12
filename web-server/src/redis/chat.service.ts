import { Injectable } from '@nestjs/common';
import { MessagingProviderInterface } from "../common/messaging-provider.interface";
import { MessageInterface } from "../websockets/messages/message.interface";
import RedisStreamListener from "./redis.stream-listener";
import RedisPubSub from "./redis.pub-sub";

@Injectable()
export default class ChatService implements MessagingProviderInterface {
    private isInit = false;

    // private workerCallback: (msg:Object) => void;

    private chatCallback: (msg:Object) => void;

    private redisStreamListener: RedisStreamListener;

    // private redisPubSub: RedisPubSub;

    private readonly CHAT_PREFIX = 'chat';

    private chats: Map<number, (msg: string) => void> = new Map<number, (msg:string) => void>();

    // private pendingNewGames: Map<number, Array<{worker: string, gamesCount: number}>> = new Map();

    async init(/*workerCallback: (message: Object) => void,*/ chatCallback: (message: Object) => void): Promise<void> {
        if (!this.isInit) {
            // this.workerCallback = workerCallback;
            this.chatCallback = chatCallback;

            this.redisStreamListener = new RedisStreamListener();
            await this.redisStreamListener.init();

            // this.redisPubSub = new RedisPubSub();
            // await this.redisPubSub.init(this.workerCallback, this.onNewGameMessageCallback);

            this.isInit = true;
        }
    }

    // private onNewGameMessageCallback = (gameId: string, workerName: string, gamesCount: number) => {
    //     this.pendingNewGames.get(parseInt(gameId)).push({
    //         worker: workerName,
    //         gamesCount: gamesCount
    //     })
    // }

    async sendToChat(lobbyId: number, message: MessageInterface): Promise<void> {
        this.subscribeToChat(lobbyId);
        await this.redisStreamListener.send(`${this.CHAT_PREFIX}${lobbyId}`, JSON.stringify(message));
    }

    // sendToWorkersTest(message): void {
    //     this.redisPubSub.sendToWorkerTest(message);
    // }

    async getWholeChat(lobbyId: number, cb: (msg: Object) => void) {
        const chatCb = (msg: string) => {
           const data = JSON.parse(msg);
           cb(data)
        };
        return this.redisStreamListener.addListener(`${this.CHAT_PREFIX}${lobbyId}`,'0', chatCb, true);
    }

    async subscribeToChat(lobbyId: number): Promise<void> {
        if (this.chats.has(lobbyId)) return ;
        const chatCb = (msg: string) => {
            const data = JSON.parse(msg);
            this.chatCallback(data);
        };
        this.chats.set(lobbyId, chatCb);
        this.redisStreamListener.addListener(`${this.CHAT_PREFIX}${lobbyId}`, '0', chatCb);
    }

    async unsubscribeFromChat(lobbyId: number): Promise<void> {
        if (!this.chats.has(lobbyId)) return ;
        this.redisStreamListener.removeListener(`${this.CHAT_PREFIX}${lobbyId}`, this.chats.get(lobbyId));
        this.chats.delete(lobbyId);
    }

    async waitForInit(): Promise<boolean> {
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

}
