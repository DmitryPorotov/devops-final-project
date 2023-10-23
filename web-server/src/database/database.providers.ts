import { DataSource } from 'typeorm';
import constants from '../constants';
import { env } from 'process'

export const databaseProviders = [
    {
        provide: constants.DATA_SOURCE,
        useFactory: async () => {
            const dataSource = new DataSource({
                type: 'mariadb',
                host: env.DB_HOST,
                port: 3306,
                username: env.DB_USER,
                password: env.DB_PASSWORD,
                database: env.DB_NAME,
                entities: [
                    __dirname + '/../**/*.entity{.ts,.js}',
                ],
                synchronize: true,
            });

            return dataSource.initialize();
        },
    },
];
