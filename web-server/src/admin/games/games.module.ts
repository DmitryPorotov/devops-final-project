import { Module } from '@nestjs/common';
import { GamesController } from './games.controller';
import { GamesService } from './games.service';
import { JwtModule } from '@nestjs/jwt';
import { EventsModule } from '../../websockets/events.module';

@Module({
  controllers: [GamesController],
  providers: [GamesService],
  imports: [JwtModule, EventsModule],
})
export class GamesModule {}
