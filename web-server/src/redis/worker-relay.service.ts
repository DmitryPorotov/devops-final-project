import RedisPubSub from "./redis.pub-sub"
import { Injectable, Logger } from "@nestjs/common"
import RedisCacheStorage from "./redis.cache-storage"

@Injectable()
class WorkerRelayService {
    private readonly logger = new Logger(WorkerRelayService.name);
    private isInit = false;

    private redisPubSub: RedisPubSub;

    private redisCacheStorage: RedisCacheStorage;

    private workerCallback: (msg:Object) => void;

    private pendingNewGames: Map<number, Array<{worker: string, gamesCount: number}>> = new Map();

    private gameSubscriptions: Map<number, true> = new Map<number, true>();

    async init(workerCallback: (message: Object) => void): Promise<void> {
        if (!this.isInit) {
            this.workerCallback = workerCallback;


            this.redisPubSub = new RedisPubSub();
            await this.redisPubSub.init(this.workerCallback, this.onNewGameMessageCallback);

            this.redisCacheStorage = new RedisCacheStorage();
            await this.redisCacheStorage.init();

            this.isInit = true;
        }
    }
    private onNewGameMessageCallback = (gameId: string, workerName: string, gamesCount: number) => {
        this.logger.debug('in worker new game message')
        this.pendingNewGames.get(parseInt(gameId)).push({
            worker: workerName,
            gamesCount: gamesCount
        })
    }

    sendToWorkersTest(message): void {
        this.redisPubSub.sendToWorkerTest(message);
    }

    private gameCb = (message: string, channel: string) => {
        const data = JSON.parse(message);
        this.workerCallback(data);
    }

    async subscribeToGame(gameId: number) {
        if (this.gameSubscriptions.has(gameId)) return ;
        await this.redisPubSub.subscribe(`game${gameId}.*`, this.gameCb)
        this.gameSubscriptions.set(gameId,true);
    }

    async unsubscribeFromGame(gameId: number) {
        if (!this.gameSubscriptions.has(gameId)) return ;
        await this.redisPubSub.unsubscribe(`game${gameId}.*`)
        this.gameSubscriptions.delete(gameId);
    }

    async sendToWorker(gameId: number, message: string) {
        const workerName = await this.redisCacheStorage.get(`game:${gameId}`);
        this.redisPubSub.publishToWorker(workerName, message)
    }

    async sendToGame(gameId: number, message: string) {
        const workerName = await this.redisCacheStorage.get(`game:${gameId}`);
        this.redisPubSub.publishToGame(workerName, `game:${gameId}`, message);
    }

    async createNewGame(userId: number, gameId: number) {
        this.redisPubSub.publishToWorker( 'new_game', JSON.stringify({
            userId,
            gameId: String(gameId),
            action: 'new_game'
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
                }))
                this.redisCacheStorage.set(`game:${gameId}`, worker.worker);
            }
            else {
                this.workerCallback({
                    gameId,
                    error: "Workers are busy or are dead."
                })
            }
            this.pendingNewGames.delete(gameId);
        }, 1000)
    }
}

export default WorkerRelayService;