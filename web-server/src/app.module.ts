import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { LobbyModule } from './lobby/lobby.module';
import {JwtModule} from "@nestjs/jwt";
import {EventsModule} from "./websockets/events.module";
import { ConfigModule } from '@nestjs/config';
import { RedisModule } from './redis/redis.module';
import {AdminModule} from "./admin/admin.module";
import {GamesModule} from "./admin/games/games.module";



@Module({
  imports: [ConfigModule.forRoot({
    isGlobal: true,
  }), UserModule, AuthModule, LobbyModule, JwtModule, EventsModule, RedisModule, AdminModule, GamesModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
