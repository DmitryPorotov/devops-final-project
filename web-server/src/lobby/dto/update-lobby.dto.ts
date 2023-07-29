import {ApiProperty, PartialType} from '@nestjs/swagger';
import { CreateLobbyDto } from './create-lobby.dto';
import {Length} from "class-validator";

export class UpdateLobbyDto extends PartialType(CreateLobbyDto) {
    @ApiProperty()
    @Length(5)
    name?: string;

    @ApiProperty()
    @Length(4)
    password?: string;
}
