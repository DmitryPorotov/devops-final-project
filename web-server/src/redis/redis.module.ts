import { Module } from '@nestjs/common';
import ChatService from './chat.service';
import WorkerRelayService from "./worker-relay.service"
import RedisStreamListener from "./redis.stream-listener";
import GameTransferService from "./game-transfer.service";

@Module({
  providers: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
      GameTransferService,
  ],
  exports: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
  ]
})
export class RedisModule {}
