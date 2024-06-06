import { Module } from '@nestjs/common';
import ChatService from './chat.service';
import WorkerRelayService from "./worker-relay.service"
import RedisStreamListener from "./redis.stream-listener";
import GameTransferService from "./game-transfer.service";
import MessageResendService from "./message-resend.service";

@Module({
  providers: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
      GameTransferService,
      MessageResendService,
  ],
  exports: [
      ChatService,
      WorkerRelayService,
      RedisStreamListener,
      MessageResendService,
  ]
})
export class RedisModule {}
