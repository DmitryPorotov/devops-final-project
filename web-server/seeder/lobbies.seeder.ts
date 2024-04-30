import {Seeder} from "nestjs-seeder";
import {Inject, Injectable} from "@nestjs/common";
import constants from "../src/constants";
import {DataSource, Repository} from "typeorm";
import {Lobby} from "../src/lobby/entities/lobby.entity";
import {User} from "../src/user/entities/user.entity";

@Injectable()
export class LobbiesSeeder implements Seeder {
    constructor(
        @Inject(constants.LOBBY_REPOSITORY)
        private lobbyRepository: Repository<Lobby>,
        @Inject(constants.DATA_SOURCE)
        private dataSource: DataSource,
    ) {
    }

    async drop(): Promise<any> {
        await this.dataSource.createQueryRunner().query("TRUNCATE TABLE lobby_participants_user");
        return this.lobbyRepository.clear();
    }

    async seed(): Promise<any> {
        const user = new User();
        user.id = 1;

        const lobbies = [];
        for (const i of [1,2,3,4,5]) {
            const lobby = new Lobby();
            lobby.name = 'test' + i;
            if (i === 1) lobby.password = '1234';
            lobby.owner = user;
            lobby.participants = [];
            lobby.participants.push(user);
            lobbies.push(lobby)
        }

        for (const i of [2,3,4,5,6]) {
            const user = new User();
            user.id = i;
            lobbies[1].participants.push(user);
        }

        return this.lobbyRepository.save(lobbies);
    }

}
