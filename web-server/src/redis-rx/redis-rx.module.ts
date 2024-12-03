import { Module } from '@nestjs/common';
import RedisSubscribeService from './redis.subscribe.service';
import RedisRouterService from './redis.router.service';
import RedisPublishService from './redis.publish.service';
import { RedisModule } from '../redis/redis.module';
import RedisChatService from './redis.chat.service';
import RedisClientsService from './redis.clients.service';

@Module({
  imports: [RedisModule],
  providers: [
    RedisSubscribeService,
    RedisRouterService,
    RedisPublishService,
    RedisChatService,
    RedisClientsService,
  ],
  exports: [RedisRouterService],
})
export class RedisRxModule {}
