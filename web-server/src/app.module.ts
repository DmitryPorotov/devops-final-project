import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { LobbyModule } from './lobby/lobby.module';
import {JwtModule} from "@nestjs/jwt";


@Module({
  imports: [UserModule, AuthModule, LobbyModule, JwtModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
