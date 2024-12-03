import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  Req,
  UseGuards,
} from '@nestjs/common';
import { Roles } from '../../auth/roles.decorator';
import { AuthGuard } from '../../auth/auth.guard';
import { GamesService } from './games.service';
import { Request } from 'express';

@Roles('admin')
@UseGuards(AuthGuard)
@Controller('admin/games')
export class GamesController {
  constructor(private readonly gamesService: GamesService) {}

  @Get(':id')
  async state(@Param('id') id: string, @Req() request: Request) {
    return await this.gamesService.getState(id, request['user']);
  }

  @Post(':id')
  async save(
    @Param('id') id: string,
    @Body() { name }: { name: string },
    @Req() request: Request,
  ) {
    return await this.gamesService.saveGame(id, name, request['user']);
  }

  @Get(':userId/saves')
  async listSaves(@Param('userId') userId: string, @Req() request: Request) {
    return await this.gamesService.listSaves(+userId, request['user']);
  }

  @Post(':lobbyId/load')
  async loadGame(
    @Param('lobbyId') lobbyId: string,
    @Body() { name }: { name: string },
    @Req() request: Request,
  ) {
    return await this.gamesService.loadGame(lobbyId, name, request['user']);
  }
}
