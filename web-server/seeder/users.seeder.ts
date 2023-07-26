import {Inject, Injectable} from "@nestjs/common";
import {Seeder} from "nestjs-seeder";
import {User} from "../src/user/entities/user.entity";
import * as constants from "../src/constants";
import {Repository} from "typeorm";

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
        user.password = '$2b$10$lZhQp13Dw3mzkGbnO8pbCOd4zObY7fH42Cp4jX93/U8w8amSuE46a';
        user.name = 'user1';
        user.isEnabled = true;

        const admin = new User();
        admin.email = 'admin@b.com';
        admin.password = '$2b$10$zZ19qsEcXp6XbZBsya.j2ObBPmjN4OdOHzteLXoc..Ug2urCFiI5.';
        admin.name = 'admin1';
        admin.isAdmin = true;
        admin.isEnabled = true;

        return this.userRepository.insert([user, admin]);
    }

    async drop(): Promise<any> {
        return this.userRepository.clear();
    }
}
