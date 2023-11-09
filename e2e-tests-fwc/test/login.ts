import * as http from 'http';
import { setTimeout } from "timers"

export interface LoginUserDto {
    id: number;

    email: string;

    name: string;

    token: string;
}

export async function login({email, password}): Promise<LoginUserDto> {
    return await send('/auth/login', JSON.stringify({
        email,
        password,
    })) as LoginUserDto
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
            hostname: 'localhost',
            port: 3001,
            path,
            headers,
            method,

        }, (res) => {
            if (!String(res.statusCode).startsWith("2") ) {
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

export async function sleep(t) {
    return new Promise<void>(resolve => {
        setTimeout(()=>resolve(), t);
    })
}