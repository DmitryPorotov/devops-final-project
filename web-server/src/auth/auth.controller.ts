import {
  Body,
  Controller,
  InternalServerErrorException,
  Logger,
  Post,
  UnauthorizedException,
} from '@nestjs/common';
import { CreateUserDto } from '../user/dto/create-user.dto';
import { UserService } from '../user/user.service';
import { AuthCredentialsDto } from './dto/auth.credentials.dto';

@Controller('auth')
export class AuthController {
  private readonly logger: Logger = new Logger(AuthController.name);

  constructor(private readonly userService: UserService) {}

  @Post('/signup')
  async signUp(@Body() createUserDto: CreateUserDto) {
    try {
      await this.userService.create(createUserDto);
      return this.userService.login(createUserDto);
    } catch (e) {
      this.logger.error(
        e,
        e?.stack,
        'Something went wrong when signing up an user.',
      );
      throw new InternalServerErrorException('Error saving a user.');
    }
  }

  @Post('/login')
  async login(@Body() authCredentialsDto: AuthCredentialsDto) {
    const loggedInUser = await this.userService.login(authCredentialsDto);
    if (!loggedInUser) throw new UnauthorizedException();
    return loggedInUser;
  }
}
