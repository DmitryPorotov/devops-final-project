import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import {UserService} from "../user/user.service";
import {DatabaseModule} from "../database/database.module";
import {userProviders} from "../user/entities/user.providers";
import {JwtModule} from "@nestjs/jwt";

@Module({
  imports: [DatabaseModule, JwtModule],
  providers: [AuthService, UserService, ...userProviders],
  controllers: [AuthController],
})
export class AuthModule {}
