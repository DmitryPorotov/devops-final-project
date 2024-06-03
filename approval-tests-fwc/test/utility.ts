import {setTimeout} from "timers";
import * as http from "http";
import settings from './settings'

export interface LoginUserDto {
    id: number;

    email: string;

    name: string;

    token: string;
}

export function makeMessageId() {
    return "" + Math.random() + Math.random();
}

export async function send(path: string, message: string, token?: string | null, method = 'POST', port = null) {
    return new Promise((resolve, reject) => {
        const headers = {
            "Content-Type": "application/json",
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        const request = http.request({
            hostname: settings.host,
            port: port || settings.port,
            path: settings.apiPath + path,
            headers,
            method,

        }, (res) => {
            if (!String(res.statusCode).startsWith("2")) {
                reject(new Error('Unsuccessful request'))
            }
            const chunks: any[] = [];
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
