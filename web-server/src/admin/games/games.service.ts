import { Injectable } from '@nestjs/common';
import RedisPubSub from "../../redis/redis.pub-sub";

@Injectable()
export class GamesService {
    async getState(id: string): Promise<any> {
        return this.sendToWorker({
            gameId: id,
            userId: -1,
            messageId: "" + Math.random(),
            action: 'get_game_state',
        });
    }

    async saveGame(id: string, name: string): Promise<any> {
        this.sendToWorker({
            gameId: id,
            userId: -1,
            messageId: "" + Math.random(),
            action: 'save',
            saveName: name,
        })
    }

    private async sendToWorker(json: Object): Promise<any> {
        const redisPubSub = new RedisPubSub();
        await redisPubSub.init(()=>{},()=>{});
        return new Promise(async (resolve, reject) => {
            try {
                await redisPubSub.subscribe(`admin.*`, (message: string, channel: string) => {
                    resolve(message);
                    redisPubSub.unsubscribe(`admin.*`)
                });

                await redisPubSub.publishToGame('worker1' /*todo*/, 'admin', JSON.stringify(json))
            }
            catch (e) {
                reject(e)
            }
        });
    }
}
