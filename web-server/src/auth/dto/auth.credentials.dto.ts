import {ApiProperty} from "@nestjs/swagger";
import {IsEmail, IsNotEmpty, Length} from "class-validator";

export class AuthCredentialsDto {
    @ApiProperty()
    @IsNotEmpty()
    @IsEmail()
    email: string;

    @ApiProperty()
    @IsNotEmpty()
    @Length(8)
    password: string;
}