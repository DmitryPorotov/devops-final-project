import { Injectable, Logger } from "@nestjs/common";
import RedisPubSub from "./redis.pub-sub";

@Injectable()
class GameTransferService {
    private readonly logger = new Logger(GameTransferService.name);

    private workerReplies: {worker: string, gamesCount: number}[] = [];

    public addWorkerReply(reply: {worker: string, gamesCount: number}) {
        this.workerReplies.push(reply)
    }

    private _isTransferInProgress = false;
    public get isTransferInProgress(): boolean {
        return this._isTransferInProgress
    }

    public async transferGames(redisPubSub: RedisPubSub, games: {[key: string]: string}) {
        this._isTransferInProgress = true;
        redisPubSub.publishToWorker( 'new_game', JSON.stringify({
            userId: -1,
            gameId: -1,
            action: 'new_game',
            messageId: "" + Math.random()
        }));
        return new Promise<void>((resolve, reject)=> {
            setTimeout(()=> {
                const numberOfGamesToTransfer = Object.keys(games).length;
                const numberOfGamesAtWorkers =
                    this.workerReplies.reduce<number>((acc, cur) => acc += cur.gamesCount, 0);
                const totalNumberOfGames = numberOfGamesToTransfer + numberOfGamesAtWorkers;
                const desiredNumberGamesPerWorker = Math.ceil(totalNumberOfGames / this.workerReplies.length);
                const numberOfGamesToTransferToWorkers: {worker: string, num: number}[] = [];
                for (const reply of this.workerReplies) {
                    if (reply.gamesCount < desiredNumberGamesPerWorker) {
                        numberOfGamesToTransferToWorkers.push({
                            worker: reply.worker,
                            num: desiredNumberGamesPerWorker - reply.gamesCount
                        })
                    }
                }
                const gamesArray = [];
                for (let gameId in games) {
                    gamesArray.push({
                        id: gameId,
                        uuid: games[gameId]
                    })
                }
                for (const worker of numberOfGamesToTransferToWorkers) {
                    //todo
                }
            }, 1000);
        });
    }
}

export default GameTransferService;
