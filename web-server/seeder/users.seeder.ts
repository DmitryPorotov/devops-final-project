import {Inject, Injectable} from "@nestjs/common";
import {Seeder} from "nestjs-seeder";
import {User} from "../src/user/entities/user.entity";
import constants from "../src/constants";
import {Repository} from "typeorm";
import * as bcrypt from "bcrypt"

@Injectable()
export class UsersSeeder implements Seeder {
    constructor(
        @Inject(constants.USER_REPOSITORY)
        private userRepository: Repository<User>
    ) {
    }

    async seed(): Promise<any> {
        const user = new User();
        user.email = 'a@b.com';
        user.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        user.name = 'user1';
        user.isEnabled = true;

        const user2 = new User();
        user2.email = 'b@b.com';
        user2.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        user2.name = 'user2';
        user2.isEnabled = true;

        const admin = new User();
        admin.email = 'admin@b.com';
        admin.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        admin.name = 'admin1';
        admin.isAdmin = true;
        admin.isEnabled = true;

        const user3 = new User();
        user3.email = 'c@b.com';
        user3.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        user3.name = 'user3';
        user3.isEnabled = true;

        const user4 = new User();
        user4.email = 'd@b.com';
        user4.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        user4.name = 'user4';
        user4.isEnabled = true;

        const user5 = new User();
        user5.email = 'e@b.com';
        user5.password = await bcrypt.hash('12345678', constants.HASH_SALT_ROUNDS);
        user5.name = 'user5';
        user5.isEnabled = true;

        return this.userRepository.insert([user, user2, admin, user3, user4, user5]);
    }

    async drop(): Promise<any> {
        return this.userRepository.clear();
    }
}
