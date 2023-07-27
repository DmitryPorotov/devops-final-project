import { seeder } from "nestjs-seeder";
import {DatabaseModule} from "./database/database.module";
import {UsersSeeder} from "../seeder/users.seeder";
import {userProviders} from "./user/entities/user.providers";
import {LobbiesSeeder} from "../seeder/lobbies.seeder";
import {lobbyProviders} from "./lobby/entities/lobby,providers";


seeder({
    imports:[
        DatabaseModule
    ],
    providers: [
        ...userProviders, ...lobbyProviders
    ]

}).run([UsersSeeder, LobbiesSeeder]);
