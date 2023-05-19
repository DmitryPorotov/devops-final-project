import {Entity, Column, CreateDateColumn, PrimaryGeneratedColumn, UpdateDateColumn, DeleteDateColumn} from 'typeorm';

@Entity()
export class User {
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
        default: false
    })
    isEnabled: boolean;

    @Column('bool', {
        default: false
    })
    isAdmin: boolean;

    @CreateDateColumn()
    createdAt: Date;

    @UpdateDateColumn()
    updatedAt: Date;

    @DeleteDateColumn({
        nullable: true,
    })
    deletedAt?: Date;
}
