import {
    Entity,
    Column,
    PrimaryGeneratedColumn, ManyToOne, ManyToMany, JoinTable,
} from 'typeorm';
import {BaseEntity} from "../../common/base.entity";
import {User} from "../../user/entities/user.entity";

@Entity()
export class Lobby extends BaseEntity {
    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    name: string;

    @Column({
        nullable:true
    })
    password?: string;

    @ManyToOne(() => User, (user) => user.ownedLobbies, {createForeignKeyConstraints: false})
    @Column({
        type: "int"
    })
    owner: User;

    @ManyToMany(() => User, (user) => user.lobbies, {createForeignKeyConstraints: false})
    @JoinTable()
    participants: User[];
}
