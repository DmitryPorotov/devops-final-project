import {env} from 'process'

export default {
    DATA_SOURCE: 'DATA_SOURCE_DEV',
    USER_REPOSITORY: 'USER_REPOSITORY',
    LOBBY_REPOSITORY: 'LOBBY_REPOSITORY',
    HASH_SALT_ROUNDS: 10,
    JWT_SECRET: env.JWT_SECRET || 'bottom-secret',
    WS_PING_INTERVAL: 20000,
    CHAT_PREFIX: 'chat',
};

