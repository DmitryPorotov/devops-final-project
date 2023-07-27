import { DataSource } from 'typeorm';
import { User } from './user.entity';
import constants from '../../constants';

export const userProviders = [
    {
        provide: constants.USER_REPOSITORY,
        useFactory: (dataSource: DataSource) => dataSource.getRepository(User),
        inject: [constants.DATA_SOURCE],
    },
];
