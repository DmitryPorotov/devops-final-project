import {ApiProperty} from "@nestjs/swagger";
import {IsNotEmpty, Length} from "class-validator";

export class CreateLobbyDto {
    @ApiProperty()
    @IsNotEmpty()
    name: string;

    @ApiProperty()
    @IsNotEmpty()
    @Length(4)
    password: string;
}
