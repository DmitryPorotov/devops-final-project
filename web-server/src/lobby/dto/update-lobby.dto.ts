import {ApiProperty, PartialType} from '@nestjs/swagger';
import { CreateLobbyDto } from './create-lobby.dto';
import { IsOptional, Length } from "class-validator"

export class UpdateLobbyDto extends PartialType(CreateLobbyDto) {
    @ApiProperty()
    @Length(5)
    name?: string;

    @ApiProperty()
    @Length(4)
    @IsOptional()
    password?: string;

    @ApiProperty()
    @IsOptional()
    deletePassword?: boolean
}
