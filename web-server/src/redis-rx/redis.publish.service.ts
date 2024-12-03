import { Injectable, Logger } from '@nestjs/common';
import { RedisClientType } from 'redis';
import { env } from 'process';
import RedisClientsService from './redis.clients.service';

@Injectable()
class RedisPublishService {
  private readonly logger = new Logger(RedisPublishService.name);

  private readonly SERVER_NAME = env.SERVER_NAME;

  private redisPublisher: RedisClientType;

  constructor(private redisClientsService: RedisClientsService) {}

  async init() {
    if (this.redisPublisher) return;
    this.redisPublisher = await this.redisClientsService.getNewClient();
  }

  publishToLobby(workerName: string, lobbyId: number, message: string) {
    this.redisPublisher.publish(`${workerName}.game${lobbyId}`, message);
  }

  publishToChannel(channel: string, message: string) {
    this.redisPublisher.publish(`${channel}.${this.SERVER_NAME}`, message);
  }
}

export default RedisPublishService;
