import { Module } from '@nestjs/common';
import { EventsGateway } from './events.gateway';
import {JwtModule} from "@nestjs/jwt";
import {AuthGuard} from "../auth/auth.guard";
import WebsocketService from "./websocket.service";
import {LobbyService} from "../lobby/lobby.service";
import {lobbyProviders} from "../lobby/entities/lobby,providers";
import {DatabaseModule} from "../database/database.module";
import LobbyManagerService from "./lobby/lobby-manager.service";
import ConnectivityTestService from "./connectivity-test.service";
import { RedisModule } from "../redis/redis.module";
import LobbiesClientsMapService from "./lobbies-clients-map.service"
import GameMessagingService from "./game/game-messaging.service"
import SystemMessageService from "./system-message.service";

@Module({
    imports:[JwtModule, DatabaseModule, RedisModule],
    providers: [
        EventsGateway,
        AuthGuard,
        WebsocketService,
        LobbyManagerService,
        LobbiesClientsMapService,
        GameMessagingService,
        LobbyService,
        ConnectivityTestService,
        SystemMessageService,
        ...lobbyProviders
    ],
})
export class EventsModule {}