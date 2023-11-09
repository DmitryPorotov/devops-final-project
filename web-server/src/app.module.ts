import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { LobbyModule } from './lobby/lobby.module';
import {JwtModule} from "@nestjs/jwt";
import {EventsModule} from "./websockets/events.module";
import { RabbitMqModule } from './rabbit-mq/rabbit-mq.module';
import { ConfigModule } from '@nestjs/config';
import { RedisModule } from './redis/redis.module';


@Module({
  imports: [ConfigModule.forRoot({
    isGlobal: true,
  }), UserModule, AuthModule, LobbyModule, JwtModule, EventsModule, RabbitMqModule, RedisModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
