import {Controller, Get, Post, Body, Patch, Param, Delete, Req, UseGuards} from '@nestjs/common';
import { LobbyService } from './lobby.service';
import { CreateLobbyDto } from './dto/create-lobby.dto';
import { UpdateLobbyDto } from './dto/update-lobby.dto';
import {Roles} from "../auth/roles.decorator";
import {FastifyRequest} from 'fastify';
import {AuthGuard} from "../auth/auth.guard";
import {User} from "../user/entities/user.entity";

@Controller('lobby')
@UseGuards(AuthGuard)
export class LobbyController {
  constructor(private readonly lobbyService: LobbyService) {}

  @Post()
  @Roles('loggedIn')
  create(
      @Body() createLobbyDto: CreateLobbyDto,
      @Req() request: FastifyRequest
  ) {
    return this.lobbyService.create(createLobbyDto, request['user'] as User);
  }

  @Get()
  findAll() {
    return this.lobbyService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.lobbyService.findOne(+id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updateLobbyDto: UpdateLobbyDto) {
    return this.lobbyService.update(+id, updateLobbyDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.lobbyService.remove(+id);
  }
}
