import { Injectable, Inject } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import * as constants from '../constants';
import {QueryFailedError, Repository} from 'typeorm';
import { User } from "./entities/user.entity";
import * as bcrypt from 'bcrypt';

@Injectable()
export class UserService {
  constructor(
      @Inject(constants.USER_REPOSITORY)
      private userRepository: Repository<User>
  ) {
  }

  async create(createUserDto: CreateUserDto) {
    const hashed = await bcrypt.hash(
        createUserDto.password,
        constants.HASH_SALT_ROUNDS
    );
    const user = new User();
    user.email = createUserDto.email;
    user.password = hashed;
    user.name = createUserDto.name;
    try {
      return await this.userRepository.save(user);
    }
    catch (e) {
      if (
          e instanceof QueryFailedError
          && (
              ((e as unknown) as {code: string}).code === "ER_DUP_ENTRY"
              ||
              ((e as unknown) as {code: string}).code === "23505"
          )
      )
      {
        const oldUser = await this.userRepository.findOne({
          where:{
            email: createUserDto.email
          },
          withDeleted: true
        });
        if (oldUser.deletedAt != null) {
          oldUser.name = createUserDto.name;
          oldUser.password = hashed;
          oldUser.deletedAt = null;
          oldUser.createdAt = new Date();
          oldUser.updatedAt = new Date();
          oldUser.isEnabled = false;
          oldUser.passwordResetToken = null;
          oldUser.rememberToken = null;
          return this.userRepository.save(oldUser);
        }
      }
      throw e;
    }
  }

  async findAll(): Promise<User[]> {
    return this.userRepository.find();
  }

  async findOne(id: number): Promise<User | null> {
    return this.userRepository.findOneBy({
      id
    });
  }

  async update(id: number, updateUserDto: UpdateUserDto) {
    const user = await this.userRepository.findOneBy({
      id
    });
    if (updateUserDto.name != null) {
      user.name = updateUserDto.name;
    }
    if (updateUserDto.password != null) {
      user.password = await bcrypt.hash(updateUserDto.password, constants.HASH_SALT_ROUNDS);
    }

    return this.userRepository.save(user);
  }

  async remove(id: number) {
    return this.userRepository.softRemove({id});
  }
}
