import {INestApplication} from "@nestjs/common";
import initApp from "./init-app";
import * as request from "supertest";
import {LoginUserDto} from "../src/user/dto/login-user.dto";
import {expect, describe, it,beforeAll,afterAll} from '@jest/globals';

describe('AuthController', () => {
    let app: INestApplication;

    beforeAll(async () => {
        app = await initApp();
    });

    afterAll(async () => {
        await app.close();
    });

    it('POST /auth/signup', () => {
        return request(app.getHttpServer())
            .post('/auth/signup')
            .send({
                email: 'qwe3@qwe.com',
                password: '12345678',
                name: 'user3'
            })
            .expect(201);
    });

    it('POST /auth/login', () => {
       return request(app.getHttpServer())
           .post('/auth/login')
           .send({
               email: 'a@b.com',
               password: '12345678'
           })
           .expect(201)
           .expect(r => {
               const b: LoginUserDto = r.body;
               expect(b.name).toBe('user1');
               expect(b.email).toBe('a@b.com');
               expect(b.token).toBeTruthy();
           })
    });
});
