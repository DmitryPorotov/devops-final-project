import {ApiProperty} from "@nestjs/swagger";
import {Length} from "class-validator";

export class JoinLobbyDto {
    @ApiProperty()
    @Length(4)
    password?: string;
}
