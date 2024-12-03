import { DataSource } from 'typeorm';
import { Lobby } from './lobby.entity';
import constants from '../../constants';
import { Provider } from '@nestjs/common';

export const lobbyProviders: Provider[] = [
  {
    provide: constants.LOBBY_REPOSITORY,
    useFactory: (dataSource: DataSource) => dataSource.getRepository(Lobby),
    inject: [constants.DATA_SOURCE],
  },
];
