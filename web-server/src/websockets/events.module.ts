import { Module } from '@nestjs/common';
import { EventsGateway } from './events.gateway';
import {JwtModule} from "@nestjs/jwt";
import {AuthGuard} from "../auth/auth.guard";
import WebsocketService from "./websocket.service";
import {LobbyService} from "../lobby/lobby.service";
import {lobbyProviders} from "../lobby/entities/lobby,providers";
import {DatabaseModule} from "../database/database.module";
import LobbyManagerService from "./lobby-manager.service";
import {RabbitMqModule} from "../rabbit-mq/rabbit-mq.module";
import ConnectivityTestService from "./connectivity-test.service";
import { RedisModule } from "../redis/redis.module";
import LobbiesClientsMapService from "./lobbies-clients-map.service"

@Module({
    imports:[JwtModule, DatabaseModule, RabbitMqModule, RedisModule],
    providers: [EventsGateway, AuthGuard, WebsocketService, LobbyManagerService, LobbiesClientsMapService, LobbyService, ConnectivityTestService, ...lobbyProviders],
})
export class EventsModule {}