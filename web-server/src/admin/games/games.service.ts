import { Injectable } from '@nestjs/common';
import RedisPubSub from "../../redis/redis.pub-sub";

@Injectable()
export class GamesService {
    async getState(id: number): Promise<any> {
        const redisPubSub = new RedisPubSub();
        await redisPubSub.init(()=>{},()=>{});
        return new Promise(async (resolve, reject) => {
            try {
                await redisPubSub.subscribe(`admin.*`, (message: string, channel: string) => {
                    resolve(message);
                    redisPubSub.unsubscribe(`admin.*`)
                });

                await redisPubSub.publishToGame('worker1' /*todo*/, 'admin', JSON.stringify({
                    gameId: String(id),
                    userId: -1,
                    messageId: "" + Math.random(),
                    action: 'get_game_state',
                }))
            }
            catch (e) {
                reject(e)
            }
        });

    }
}
