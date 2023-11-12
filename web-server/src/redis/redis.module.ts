import { Module } from '@nestjs/common';
import ChatService from './chat.service';
import WorkerRelayService from "./worker-relay.service"

@Module({
  providers: [ChatService, WorkerRelayService],
  exports: [ChatService, WorkerRelayService]
})
export class RedisModule {}
