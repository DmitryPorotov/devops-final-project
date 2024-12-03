import { Injectable, Logger } from '@nestjs/common';
import { RedisClientType, createClient } from 'redis';
import { env } from 'process';

@Injectable()
class RedisClientsService {
  private readonly logger = new Logger(RedisClientsService.name);

  private _client: RedisClientType;

  init() {
    if (this._client == null) {
      this._client = createClient({ url: env.REDIS_URL });
    }
  }

  public async getNewClient(): Promise<RedisClientType> {
    const newClient = this._client.duplicate();
    newClient.on('error', (err) =>
      this.logger.error('Redis Client Error', err),
    );
    await newClient.connect();
    return newClient;
  }
}

export default RedisClientsService;
