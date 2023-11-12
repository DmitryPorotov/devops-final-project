import RedisPubSub from "./redis.pub-sub"
import { Injectable } from "@nestjs/common"

@Injectable()
class WorkerRelayService {
    private isInit = false;

    private redisPubSub: RedisPubSub;

    private workerCallback: (msg:Object) => void;

    private pendingNewGames: Map<number, Array<{worker: string, gamesCount: number}>> = new Map();

    async init(workerCallback: (message: Object) => void): Promise<void> {
        if (!this.isInit) {
            this.workerCallback = workerCallback;


            this.redisPubSub = new RedisPubSub();
            await this.redisPubSub.init(this.workerCallback, this.onNewGameMessageCallback);

            this.isInit = true;
        }
    }
    private onNewGameMessageCallback = (gameId: string, workerName: string, gamesCount: number) => {
        this.pendingNewGames.get(parseInt(gameId)).push({
            worker: workerName,
            gamesCount: gamesCount
        })
    }

    sendToWorkersTest(message): void {
        this.redisPubSub.sendToWorkerTest(message);
    }

    async createNewGame(userId: number, gameId: number) {
        this.redisPubSub.publish( `new_game`, JSON.stringify({
            userId,
            gameId: String(gameId),
            action: 'new_game'
        }));
        this.pendingNewGames.set(gameId, []);
        setTimeout(() => {
            const workers = this.pendingNewGames.get(gameId);
            if (workers.length) {
                const worker = workers.sort((a, b) => b.gamesCount - a.gamesCount)[0];
                this.redisPubSub.publish(worker.worker, JSON.stringify({
                    action: 'create_game',
                    userId,
                    gameId: String(gameId),
                }))
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