import { PartialType } from '@nestjs/swagger';
import { CreateLobbyDto } from './create-lobby.dto';

export class UpdateLobbyDto extends PartialType(CreateLobbyDto) {}
