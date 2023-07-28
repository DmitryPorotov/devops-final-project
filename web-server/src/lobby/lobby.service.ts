import {ConflictException, Inject, Injectable} from '@nestjs/common';
import { CreateLobbyDto } from './dto/create-lobby.dto';
import { UpdateLobbyDto } from './dto/update-lobby.dto';
import {Repository} from "typeorm";
import {Lobby} from "./entities/lobby.entity";
import constants from '../constants';
import {User} from "../user/entities/user.entity";

@Injectable()
export class LobbyService {
  constructor(
      @Inject(constants.LOBBY_REPOSITORY)
      private lobbyRepository: Repository<Lobby>
  ) {
  }

  async create(createLobbyDto: CreateLobbyDto, user: User) {
    const sameName = await this.lobbyRepository.find({
      where: {name: createLobbyDto.name}
    });
    if (sameName.length) {
      throw new ConflictException('Lobby with this name already exists');
    }
    const lobby = new Lobby();
    lobby.participants = [];
    lobby.participants.push(user);
    lobby.name = createLobbyDto.name;
    lobby.password = createLobbyDto.password;
    lobby.owner = user;
    return await this.lobbyRepository.save(lobby)
  }

  async findAll() {
    return await this.lobbyRepository.find({
      relations: {
        participants: true
      },
      select: {
        id: true,
        name: true,
        participants: {
          id: true,
          name: true
        }
      }
    });
  }

  findOne(id: number) {
    return `This action returns a #${id} lobby`;
  }

  update(id: number, updateLobbyDto: UpdateLobbyDto) {
    return `This action updates a #${id} lobby`;
  }

  remove(id: number) {
    return `This action removes a #${id} lobby`;
  }
}
