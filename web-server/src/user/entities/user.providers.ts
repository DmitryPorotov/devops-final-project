import { DataSource } from 'typeorm';
import { User } from './user.entity';
import constants from '../../constants';
import { Provider } from '@nestjs/common';

export const userProviders: Provider[] = [
  {
    provide: constants.USER_REPOSITORY,
    useFactory: (dataSource: DataSource) => dataSource.getRepository(User),
    inject: [constants.DATA_SOURCE],
  },
];
