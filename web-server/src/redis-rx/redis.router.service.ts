import { Injectable, Logger } from '@nestjs/common';
import RedisSubscribeService from './redis.subscribe.service';
import { Observable } from 'rxjs';
import { WorkerMessageInterface } from '../websockets/messages/worker-message.interface';
import { filter, map, mergeWith } from 'rxjs/operators';
import RedisChatService from './redis.chat.service';
import { MessageInterface } from '../websockets/messages/message.interface';

@Injectable()
class RedisRouterService {
  private readonly logger = new Logger(RedisRouterService.name);
  private workerSubscriptions: Map<number, Observable<WorkerMessageInterface>> =
    new Map<number, Observable<WorkerMessageInterface>>();
  private chatSubscriptions: Map<number, Observable<MessageInterface>> =
    new Map<number, Observable<MessageInterface>>();
  private isInit = false;

  constructor(
    private redisSubscribeService: RedisSubscribeService,
    private redisChatService: RedisChatService,
  ) {}

  async getPlayerMessagesObservable(
    playerId: number,
    lobbyId: number,
  ): Promise<Observable<MessageInterface | WorkerMessageInterface>> {
    if (!this.isInit) {
      await this.redisChatService.init();
      await this.redisSubscribeService.init();
    }
    const workerObs = this.workerSubscriptions.has(lobbyId)
      ? this.workerSubscriptions.get(lobbyId)
      : this.workerSubscriptions
          .set(
            lobbyId,
            await this.redisSubscribeService.getLobbyMessagesFromWorkerObservable(
              lobbyId,
            ),
          )
          .get(lobbyId);
    const chatObs = this.chatSubscriptions.has(lobbyId)
      ? this.chatSubscriptions.get(lobbyId)
      : this.chatSubscriptions
          .set(
            lobbyId,
            await this.redisChatService.getLobbyChatMessagesObservable(lobbyId),
          )
          .get(lobbyId);

    chatObs.pipe(
      map((m) => {
        if (m.body.to.length && !m.body.to.includes(playerId)) return;
        return m;
      }),
      filter((m) => m != null),
    );
    return workerObs.pipe(
      map((m) => {
        if (m.userId && m.userId !== playerId) return;
        if (m.reply) {
          m.reply = m.reply.filter((r) => r.to === playerId || r.to === '*');
          if (!m.reply.length) return;
        }
        m.time = new Date().toISOString();
        return m;
      }),
      filter((m) => m != null),
      mergeWith(chatObs),
    );
  }
}

export default RedisRouterService;
