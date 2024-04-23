import { Module } from '@nestjs/common';
import ChatService from './chat.service';
import WorkerRelayService from "./worker-relay.service"
import RedisStreamListener from "./redis.stream-listener";

@Module({
  providers: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
  ],
  exports: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
  ]
})
export class RedisModule {}
