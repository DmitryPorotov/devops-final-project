import {createClient, RedisClientType} from "redis"
import { env } from "process"
import { Logger } from "@nestjs/common"

class RedisCacheStorage {
    private readonly logger = new Logger(RedisCacheStorage.name);
    private redisStorage: RedisClientType;

    async init() {
        this.redisStorage = createClient({url: env.REDIS_URL});
        this.redisStorage.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisStorage.connect();
    }

    async set(key: string, value: string) {
        await this.redisStorage.set(key, value);
    }

    async get(key: string) {
        return this.redisStorage.get(key)
    }

    async del(key: string) {
        return this.redisStorage.del(key)
    }

    async lPop(key: string) {
        return this.redisStorage.lPop(key)
    }
}

export default RedisCacheStorage;