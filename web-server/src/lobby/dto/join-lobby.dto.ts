import { ApiProperty } from '@nestjs/swagger';
import { IsOptional, Length } from 'class-validator';

export class JoinLobbyDto {
  @ApiProperty()
  @IsOptional()
  @Length(4)
  password?: string;
}
