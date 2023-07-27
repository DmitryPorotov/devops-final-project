import { Injectable, Inject } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import constants from '../constants';
import {QueryFailedError, Repository} from 'typeorm';
import { User } from "./entities/user.entity";
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt'
import {AuthCredentialsDto} from "../auth/dto/auth.credentials.dto";
import {LoginUserDto} from "./dto/login-user.dto";

@Injectable()
export class UserService {
  constructor(
      @Inject(constants.USER_REPOSITORY)
      private userRepository: Repository<User>,
      private jwtService: JwtService
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

  async login(authCredentialsDto: AuthCredentialsDto): Promise<LoginUserDto | null> {
    const user = await this.userRepository.findOneBy({
      email: authCredentialsDto.email
    });
    if (!user) return null;
    const isValidLogin = await bcrypt.compare(authCredentialsDto.password, user.password)
    if (isValidLogin) {
      return {
        email: authCredentialsDto.email,
        name: user.name,
        token: this.generateJWT(user)
      }
    }
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

  private generateJWT(user: User) {
    const today = new Date();
    const exp = new Date(today);
    exp.setDate(today.getDate() + 60);

    return this.jwtService.sign({
      id: user.id,
      name: user.name,
      email: user.email,
      isAdmin: user.isAdmin,
      isEnabled: user.isEnabled,
      exp: exp.getTime() / 1000,
    }, {secret: constants.JWT_SECRET});
  };
}
