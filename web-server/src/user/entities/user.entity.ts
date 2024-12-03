import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  OneToMany,
  ManyToMany,
} from 'typeorm';
import { BaseEntity } from '../../common/base.entity';
import { Lobby } from '../../lobby/entities/lobby.entity';

@Entity()
export class User extends BaseEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({
    unique: true,
  })
  email: string;

  @Column()
  name: string;

  @Column()
  password: string;

  @Column({
    nullable: true,
    length: 100,
  })
  passwordResetToken: string;

  @Column({
    nullable: true,
    length: 100,
  })
  rememberToken: string;

  @Column('bool', {
    default: false,
  })
  isEnabled: boolean;

  @Column('bool', {
    default: false,
  })
  isAdmin: boolean;

  @OneToMany(() => Lobby, (lobby) => lobby.owner)
  ownedLobbies: Lobby[];

  @ManyToMany(() => Lobby, (lobby) => lobby.participants)
  lobbies: Lobby[];
}
