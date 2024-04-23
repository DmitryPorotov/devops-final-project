import {setTimeout} from "timers";
import * as http from "http";
import {ErrorEvent, WebSocket} from "ws"
import settings from './settings'

export interface LoginUserDto {
    id: number;

    email: string;

    name: string;

    token: string;
}

export async function send(path: string, message: string, token?: string, method = 'POST') {
    return new Promise((resolve, reject) => {
        const headers = {
            "Content-Type": "application/json",
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        const request = http.request({
            hostname: settings.host,
            port: settings.port,
            path: settings.apiPath + path,
            headers,
            method,

        }, (res) => {
            if (!String(res.statusCode).startsWith("2")) {
                reject(new Error('Unsuccessful request'))
            }
            const chunks = [];
            res.on('data', data => chunks.push(data));
            res.on('end', () => {

                const resBody = chunks.join();
                resolve(JSON.parse(resBody));
            })
        }).on('error', err => reject(err));

        request.write(message);
        request.end()
    })
}

export async function sleep(microseconds) {
    return new Promise<void>(resolve => {
        setTimeout(() => resolve(), microseconds);
    })
}

export class WebSocketWrap {
    private webSocket: WebSocket;
    public constructor(private url: string) {
        this.webSocket = new WebSocket(url);
    }

    public async open(): Promise<void> {
        return new Promise<void>((resolve, reject) => {
            this.webSocket.addEventListener("open", (event) => {
                if ((event as unknown as ErrorEvent).error) {
                    reject((event as unknown as ErrorEvent).error)
                }
                resolve();
            })
        })
    }

    public onMessage(executor, onError) {
        this.webSocket.addEventListener("message", event => {
            try {
                const json = JSON.parse(event.data as string);
                executor(json);
            }
            catch (e) {
                onError(e);
            }
        });
    }

    public sendSync(data) {
        console.log('in send sync')
        this.webSocket.send(JSON.stringify(data));
    }

    public async send(data: {messageId: string} | any): Promise<any> {
        const messageId = data.messageId;
        return new Promise<object>(async (resolve, reject) => {
            this.webSocket.addEventListener("message", event => {
                try {

                    const json = JSON.parse(event.data as string);
                    if (messageId == json.messageId) {
                        resolve(json);
                    }
                }
                catch (e) {
                    reject(e)
                }
            }, {
                once: true
            });
            this.webSocket.send(JSON.stringify(data), {}, err => {
                if (err) reject(err);
            });
        })
    }
}
