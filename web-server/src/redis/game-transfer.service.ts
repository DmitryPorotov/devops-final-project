import { Injectable, Logger } from '@nestjs/common';
import NoWorkersException from './NoWorkersException';

@Injectable()
class GameTransferService {
  private readonly logger = new Logger(GameTransferService.name);

  private workerReplies: { worker: string; gamesCount: number }[] = [];

  public addWorkerReply(reply: { worker: string; gamesCount: number }) {
    this.workerReplies.push(reply);
  }

  private _isTransferInProgress = false;
  public get isTransferInProgress(): boolean {
    return this._isTransferInProgress;
  }

  public async transferGames(games: {
    [key: string]: string;
  }): Promise<Map<string, { id: string; uuid: string }[]>> {
    this._isTransferInProgress = true;

    return new Promise<Map<string, { id: string; uuid: string }[]>>(
      (resolve, reject) => {
        setTimeout(() => {
          if (!this.workerReplies.length) {
            reject(
              new NoWorkersException('Error. No workers to transfer games to.'),
            );
          }
          const numberOfGamesToTransfer = Object.keys(games).length;
          const numberOfGamesAtWorkers = this.workerReplies.reduce<number>(
            (acc, cur) => (acc += cur.gamesCount),
            0,
          );
          const totalNumberOfGames =
            numberOfGamesToTransfer + numberOfGamesAtWorkers;
          const desiredNumberGamesPerWorker = Math.ceil(
            totalNumberOfGames / this.workerReplies.length,
          );
          const numberOfGamesToTransferToWorkers: {
            worker: string;
            num: number;
          }[] = [];
          for (const reply of this.workerReplies) {
            if (reply.gamesCount < desiredNumberGamesPerWorker) {
              numberOfGamesToTransferToWorkers.push({
                worker: reply.worker,
                num: desiredNumberGamesPerWorker - reply.gamesCount,
              });
            }
          }
          const gamesArray: { id: string; uuid: string }[] = [];
          for (let gameId in games) {
            gamesArray.push({
              id: gameId,
              uuid: games[gameId],
            });
          }
          let toSendToWorkers: Map<string, { id: string; uuid: string }[]> =
            new Map();
          outer: for (const worker of numberOfGamesToTransferToWorkers) {
            for (let i = 0; i < worker.num; i++) {
              let g = gamesArray.pop();
              if (!g) break outer;
              if (!toSendToWorkers.has(worker.worker)) {
                toSendToWorkers.set(worker.worker, []);
              }
              toSendToWorkers.get(worker.worker).push(g);
            }
          }
          this.workerReplies = [];
          this._isTransferInProgress = false;
          resolve(toSendToWorkers);
        }, 1000);
      },
    );
  }
}

export default GameTransferService;
