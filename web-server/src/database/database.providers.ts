import { DataSource } from 'typeorm';
import * as consts from '../constants';

export const databaseProviders = [
    {
        provide: consts.DATA_SOURCE,
        useFactory: async () => {
            const dataSource = new DataSource({
                type: 'mysql',
                host: 'localhost',
                port: 3306,
                username: 'bpuser',
                password: '123456',
                database: 'fwc',
                entities: [
                    __dirname + '/../**/*.entity{.ts,.js}',
                ],
                synchronize: true,
            });

            return dataSource.initialize();
        },
    },
];