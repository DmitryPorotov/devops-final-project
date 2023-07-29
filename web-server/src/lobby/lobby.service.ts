import {ConflictException, Inject, Injectable, UnauthorizedException} from '@nestjs/common';
import {CreateLobbyDto} from './dto/create-lobby.dto';
import {UpdateLobbyDto} from './dto/update-lobby.dto';
import {DataSource, Repository} from "typeorm";
import {Lobby} from "./entities/lobby.entity";
import constants from '../constants';
import {User} from "../user/entities/user.entity";
import {JoinLobbyDto} from "./dto/join-lobby.dto";
import {FindOneOptions} from "typeorm/find-options/FindOneOptions";
import {HttpException} from "@nestjs/common/exceptions/http.exception";

@Injectable()
export class LobbyService {
  constructor(
      @Inject(constants.LOBBY_REPOSITORY)
      private lobbyRepository: Repository<Lobby>,
      @Inject(constants.DATA_SOURCE)
      private dataSource: DataSource
  ) {
  }

  private async transactionWrapper(callback: (lobbyRepository: Repository<Lobby>) => Promise<Lobby>) {
    return await this.dataSource.transaction(async (em) => {
      const lobbyRepository = em.getRepository(Lobby);
      try {
        return await callback(lobbyRepository);
      } catch (e) {
        if (e instanceof HttpException) {
          throw e;
        } else {
          await lobbyRepository.queryRunner.rollbackTransaction();
        }
      }
    })
  }

  async create(createLobbyDto: CreateLobbyDto, user: User) {
    return this.transactionWrapper(async (lobbyRepository)=>{
      const sameName = await lobbyRepository.find({
        where: {name: createLobbyDto.name},
        lock: {
          mode: "pessimistic_write"
        },
        select: {
          id: true,
          name: true
        }
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
      return lobbyRepository.save(lobby);
    })
  }

  async findAll() {
    return await this.lobbyRepository.find(this.selectJoinOpt);
  }

  private selectJoinOpt: FindOneOptions<Lobby> = {
    relations: {
      participants: true,
      owner: true
    },
    select: {
      id: true,
      name: true,
      owner: {
        id: true,
        name: true
      },
      participants: {
        id: true,
        name: true
      }
    }
  };

  private selectJoinLockOpt: FindOneOptions<Lobby> = {
    ...this.selectJoinOpt,
    lock: {
      mode: "pessimistic_write"
    }
  };

  async findOne(id: number) {
    return await this.lobbyRepository.findOne({
      where: {id},
      ...this.selectJoinOpt
    });
  }

  async join(id: number, joinLobbyDto: JoinLobbyDto, user: User) {
    return this.transactionWrapper(async lobbyRepository => {
      const lobby = await lobbyRepository.findOne({
        ...this.selectJoinLockOpt,
        where: {id}
      });
      if (lobby.participants.filter(u => u.id == user.id).length) {
        throw new ConflictException('You have joined this lobby already.');
      }
      if (lobby.participants.length > 5) {
        throw new ConflictException('The lobby is full.')
      }
      if (lobby.password && lobby.password != joinLobbyDto.password) {
        throw new UnauthorizedException('The password for lobby is incorrect.');
      }
      lobby.participants.push({id: user.id, name: user.name} as User);
      return lobbyRepository.save(lobby);
    });
  }

  async leave(id: number, user: User) {
    return this.transactionWrapper(async lobbyRepository => {
      const lobby = await lobbyRepository.findOne({
        ...this.selectJoinLockOpt,
        where: {id},
      });
      if (!lobby.participants.filter(u => u.id == user.id).length) {
        throw new ConflictException('You are not in this lobby.');
      }
      lobby.participants = lobby.participants.filter(u => u.id != user.id);
      if (!lobby.participants.length) {
        return await this.remove(id, user, lobbyRepository);
      }
      if (lobby.owner.id === user.id) {
        lobby.owner = lobby.participants[0];
      }
      return lobbyRepository.save(lobby);
    });
  }

  async update(id: number, updateLobbyDto: UpdateLobbyDto, user: User) {
    const lobby = await this.findOne(id);
    if (user.id === lobby.owner.id || user.isAdmin) {
      lobby.name = updateLobbyDto.name ?? lobby.name;
      lobby.password = updateLobbyDto.password ?? lobby.password;
    }
    else throw new UnauthorizedException("You don't have permissions to edit this lobby.");
    await this.lobbyRepository.save(lobby);
    return {
      id: lobby.id,
      name: lobby.name,
      password: lobby.password
    }
  }

  async remove(id: number, user: User, lobbyRepository?: Repository<Lobby>) {
    const lobby = await this.findOne(id);
    if (lobby.owner.id != user.id || !user.isAdmin) {
      throw new UnauthorizedException("You don't have permissions to delete this lobby.");
    }
    return await (lobbyRepository || this.lobbyRepository).softRemove(lobby);
  }
}
