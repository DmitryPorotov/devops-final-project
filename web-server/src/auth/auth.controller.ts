import {Body, Controller, Post} from '@nestjs/common';
import {CreateUserDto} from "../user/dto/create-user.dto";
import {UserService} from "../user/user.service";

@Controller('auth')
export class AuthController {
    constructor(private readonly userService: UserService) {
    }

    @Post('/signup')
    async signUp(@Body() createUserDto: CreateUserDto) {
        return this.userService.create(createUserDto);
    }
}
