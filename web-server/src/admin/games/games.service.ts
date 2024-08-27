import { Injectable } from '@nestjs/common';
import RedisPubSub from "../../redis/redis.pub-sub";
import {randomUUID} from 'crypto'
import {LoginUserDto} from "../../user/dto/login-user.dto";

@Injectable()
export class GamesService {
    async getState(id: string, adminUser: LoginUserDto): Promise<object> {
        return this.sendToWorker({
            gameId: id,
            userId: -1,
            messageId: randomUUID(),
            action: 'get_game_state',
        }, adminUser.id);
    }

    async saveGame(id: string, name: string, adminUser: LoginUserDto): Promise<void> {
        this.sendToWorker({
            gameId: id,
            userId: -1,
            messageId: randomUUID(),
            action: 'save',
            saveName: name,
        }, adminUser.id)
    }

    async listSaves(userId: number, adminUser: LoginUserDto): Promise<object> {
        return this.sendToWorker({
            gameId: "3",
            userId: userId,
            messageId: randomUUID(),
            action: 'list_saves',
        }, adminUser.id)
    }

    async loadGame(lobbyId: string, name: string, adminUser: LoginUserDto): Promise<void> {
        return this.sendToWorker({
            gameId: lobbyId,
            userId: -1,
            messageId: randomUUID(),
            action: 'load',
            saveName: name
        }, adminUser.id)
    }

    private async sendToWorker(json: Object, adminId: number): Promise<any> {
        const redisPubSub = new RedisPubSub();
        await redisPubSub.init(()=>{},()=>{});
        return new Promise(async (resolve, reject) => {
            try {
                await redisPubSub.subscribe(`admin${adminId}.*`, (message: string, channel: string) => {
                    resolve(message);
                    redisPubSub.unsubscribe(`admin${adminId}.*`)
                });

                await redisPubSub.publishToGame('worker1' /*todo*/, `admin${adminId}`, JSON.stringify(json))
            }
            catch (e) {
                reject(e)
            }
        });
    }
}
