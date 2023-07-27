import {Body, Controller, Post, UnauthorizedException} from '@nestjs/common';
import {CreateUserDto} from "../user/dto/create-user.dto";
import {UserService} from "../user/user.service";
import {AuthCredentialsDto} from "./dto/auth.credentials.dto";

@Controller('auth')
export class AuthController {
    constructor(private readonly userService: UserService) {
    }

    @Post('/signup')
    async signUp(@Body() createUserDto: CreateUserDto) {
        return this.userService.create(createUserDto);
    }

    @Post('/login')
    async login(@Body() authCredentialsDto: AuthCredentialsDto) {
        const loggedInUser = await this.userService.login(authCredentialsDto);
        if (!loggedInUser) throw new UnauthorizedException();
        return loggedInUser;
    }
}
