import RedisPubSub from "./redis.pub-sub"
import { Injectable, Logger } from "@nestjs/common"
import RedisCacheStorage from "./redis.cache-storage"
import NoGameInRedisCacheError from "./NoGameInRedisCacheError";
import {WorkerMessageInterface} from "../websockets/messages/worker-message.interface";
import GameTransferService from "./game-transfer.service";

@Injectable()
class WorkerRelayService {
    private readonly logger = new Logger(WorkerRelayService.name);
    private isInit = false;

    private redisPubSub: RedisPubSub;

    private redisCacheStorage: RedisCacheStorage;

    private workerCallback: (msg:WorkerMessageInterface) => void;

    private pendingNewGames: Map<number, Array<{worker: string, gamesCount: number}>> = new Map();

    private gameSubscriptions: Map<number, true> = new Map<number, true>();

    constructor(private gameTransferService: GameTransferService) {
    }

    async init(workerCallback: (message: WorkerMessageInterface) => void): Promise<void> {
        if (!this.isInit) {
            this.workerCallback = workerCallback;

            this.redisPubSub = new RedisPubSub();
            await this.redisPubSub.init(this.workerCallback, this.onNewGameMessageCallback, this.onShutdownMessage);

            this.redisCacheStorage = new RedisCacheStorage();
            await this.redisCacheStorage.init();

            this.isInit = true;
        }
    }
    private onShutdownMessage(workerName: string) {
        if (this.gameTransferService.isTransferInProgress) {
            setTimeout(() => this.onShutdownMessage(workerName), 2000)
        }
        else {
            this.transferGames(workerName).then();
        }
    }

    private fakeGameIdForTransfer?: number;

    private async transferGames(workerName: string) {
        const listKey = `${workerName}_games`;
        const workerGamesStr = await this.redisCacheStorage.lPop(listKey);
        await this.redisCacheStorage.del(listKey);
        const workerGames = JSON.parse(workerGamesStr);
        this.fakeGameIdForTransfer = -Math.round(Math.random() * 1e+14);
        await this.gameTransferService.transferGames(this.redisPubSub, workerGames)
    }

    private onNewGameMessageCallback = (gameId: string, workerName: string, gamesCount: number) => {
        this.logger.debug('in worker new game message');
        const intId = parseInt(gameId);
        if (intId === this.fakeGameIdForTransfer) {
            this.gameTransferService.addWorkerReply({
                worker: workerName,
                gamesCount: gamesCount
            })
        }
        if (this.pendingNewGames.has(intId)) {
            this.pendingNewGames.get(intId).push({
                worker: workerName,
                gamesCount: gamesCount
            })
        }
    };

    sendToWorkersTest(message): void {
        this.redisPubSub.sendToWorkerTest(message);
    }

    private gameCb = (message: string, channel: string) => {
        const data = JSON.parse(message);
        this.workerCallback(data);
    };

    async subscribeToGame(gameId: number) {
        if (this.gameSubscriptions.has(gameId)) return ;
        await this.redisPubSub.subscribe(`game${gameId}.*`, this.gameCb);
        this.gameSubscriptions.set(gameId,true);
    }

    async unsubscribeFromGame(gameId: number) {
        if (!this.gameSubscriptions.has(gameId)) return ;
        await this.redisPubSub.unsubscribe(`game${gameId}.*`);
        this.gameSubscriptions.delete(gameId);
    }

    async sendToGame(gameId: number, message: string) {
        const workerName = await this.redisCacheStorage.get(`game:${gameId}`);
        if (!workerName) throw new NoGameInRedisCacheError();
        this.redisPubSub.publishToGame(workerName, `game${gameId}`, message);
    }

    async createNewGame(userId: number, gameId: number, isRandomHouses: boolean, messageId: string) {
        if (this.pendingNewGames.has(gameId)) return ;
        this.redisPubSub.publishToWorker( 'new_game', JSON.stringify({
            userId,
            gameId: String(gameId),
            action: 'new_game',
            messageId
        }));
        this.pendingNewGames.set(gameId, []);
        setTimeout(() => {
            const workers = this.pendingNewGames.get(gameId);
            if (workers.length) {
                const worker = workers.sort((a, b) => b.gamesCount - a.gamesCount)[0];
                this.redisPubSub.publishToWorker(worker.worker, JSON.stringify({
                    action: 'create_game',
                    userId,
                    gameId: String(gameId),
                    messageId,
                    isRandomHouses
                }));
                this.redisCacheStorage.set(`game:${gameId}`, worker.worker);
            }
            else {
                this.workerCallback({
                    gameId: "" + gameId,
                    type: "action",
                    action: "error",
                    message: "Workers are busy or are dead."
                })
            }
            this.pendingNewGames.delete(gameId);
        }, 1000)
    }
}

export default WorkerRelayService;
