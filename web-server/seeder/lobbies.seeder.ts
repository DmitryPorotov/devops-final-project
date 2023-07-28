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
        return await this.lobbyRepository.clear();
    }

    async seed(): Promise<any> {
        const user = new User();
        user.id = 1;

        const lobby1 = new Lobby();
        lobby1.name = 'test1';
        lobby1.password = '1234';
        lobby1.owner = user;
        lobby1.participants = [];
        lobby1.participants.push(user);

        const lobby2 = new Lobby();
        lobby2.name = 'test2';
        lobby2.owner = user;
        lobby2.participants = [];
        lobby2.participants.push(user);

        return await this.lobbyRepository.save([lobby1, lobby2]);
    }

}
