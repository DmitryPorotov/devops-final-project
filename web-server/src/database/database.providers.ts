import { DataSource } from 'typeorm';
import constants from '../constants';

export const databaseProviders = [
    {
        provide: constants.DATA_SOURCE,
        useFactory: async () => {
            const dataSource = new DataSource({
                type: 'mariadb',
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
