import { Module } from '@nestjs/common';
import { LobbyService } from './lobby.service';
import { LobbyController } from './lobby.controller';
import { DatabaseModule } from '../database/database.module';
import { lobbyProviders } from './entities/lobby,providers';
import { JwtModule } from '@nestjs/jwt';

@Module({
  imports: [DatabaseModule, JwtModule],
  controllers: [LobbyController],
  providers: [LobbyService, ...lobbyProviders],
})
export class LobbyModule {}
