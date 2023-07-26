import { seeder } from "nestjs-seeder";
import {DatabaseModule} from "./database/database.module";
import {UsersSeeder} from "../seeder/users.seeder";
import {UserModule} from "./user/user.module";
import {userProviders} from "./user/entities/user.providers";

seeder({
    imports:[
        UserModule, DatabaseModule
    ],
    providers: [
        ...userProviders
    ]

}).run([UsersSeeder]);
