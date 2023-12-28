import { createClient } from "redis";
import { env } from "process"
import { Logger } from "@nestjs/common"
import {sleep} from "../common/utilities";

class RedisStreamListener {
    private readonly logger = new Logger(RedisStreamListener.name);
    private redisClient;
    private listeners: {[n: string]: { callbacks: Array<(msg: string) => void>, timeout: NodeJS.Timeout }} = {};

    async init() {
        this.logger.debug('redis url ', env.REDIS_URL);
        this.redisClient = await createClient({url: env.REDIS_URL});
        this.redisClient.on('error', (err) => this.logger.error('Redis Client Error', err));
        await this.redisClient.connect();
    }

    async addListener(streamName: string, startId: string, callback: (msg: string) => void, once = false): Promise<void> {
        this.logger.debug('adding listener');
        if (!this.listeners.hasOwnProperty(streamName) || once) {
            if (!once) {
                this.logger.debug('adding new listener ' + streamName + ' id ' + startId);
                this.listeners[streamName] = {
                    callbacks: [callback],
                    timeout: null
                };
            }
            const xread = async ({ stream, id } : {stream: string, id: string}) => {
                // this.logger.debug('in xread id ' + id);
                const response = await this.redisClient.xRead({id, key: stream});

                if (response) {
                    this.logger.debug(response);
                    response.forEach(r => {
                        r.messages.forEach(message => {
                            id = message.id;
                            this.logger.debug(message.message);
                            if (once) {
                                callback(message.message.json)
                            } else {
                                this.listeners[streamName].callbacks.forEach(c => c(message.message.json));
                            }
                        })
                    })
                }
                if (!once) {
                    //recursive timeout
                    this.listeners[streamName].timeout = setTimeout(async () => await xread({
                        stream,
                        id
                    }), 20) as NodeJS.Timeout
                }
            };
            await xread({stream: streamName,id: startId});
        }
        else {
            this.listeners[streamName].callbacks.push(callback);
        }
    }

    removeListener(streamName: string, callback: (msg: string) => void): boolean {
        if (this.listeners.hasOwnProperty(streamName)) {
            let idx = this.listeners[streamName].callbacks.indexOf(callback);
            if (idx >= 0) {
                this.listeners[streamName].callbacks.splice(idx, 1);
                if (!this.listeners[streamName].callbacks.length) {
                    clearTimeout(this.listeners[streamName].timeout);
                    delete this.listeners[streamName];
                }
                return true;
            }
        }
        return false;
    }

    async send(streamName: string, message: string): Promise<string> {
        return await this.redisClient.xAdd(streamName, '*', {json: message}, {TRIM: {strategy: "MAXLEN", strategyModifier: '~', threshold: 100}});
    }
}

export default RedisStreamListener;
