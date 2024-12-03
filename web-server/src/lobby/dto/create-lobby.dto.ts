import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsOptional, Length } from 'class-validator';

export class CreateLobbyDto {
  @ApiProperty()
  @IsNotEmpty()
  @Length(5)
  name: string;

  @ApiProperty()
  @IsOptional()
  @Length(4)
  password?: string;
}
