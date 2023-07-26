import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import {User} from "../src/user/entities/user.entity";
import initApp, {getAdminToken} from './init-app';

describe('UserController', () => {
    let app: INestApplication;
    let adminToken: string;

    beforeAll(async () => {
        app = await initApp();
        adminToken = await getAdminToken(app)
    });

    afterAll(async () => {
        await app.close();
    });

    it('GET /user/1', async () => {
        return request(app.getHttpServer())
            .get('/user/1')
            .auth(adminToken, { type: "bearer" })
            .expect(200)
            .expect((r) => {
                expect(r.body.id).toBe(1);
                expect(r.body.name).toBe('user1');
            });
    });

    it('GET /user/2', () => {
        return request(app.getHttpServer())
            .get('/user/3')
            .auth(adminToken, { type: "bearer" })
            .expect(200)
            .expect(r => {
                expect(r.body).toBeFalsy();
            })
    });

    it('POST /user', () => {
        return request(app.getHttpServer())
            .post('/user')
            .auth(adminToken, { type: "bearer" })
            .send({
                email: 'qwe@qwe.com',
                password: '12345678',
                name: 'user2'
            })
            .expect(201)
            .expect(r => {
                const b: User = r.body;
                expect(b.name).toBe('user2');
                expect(b.isAdmin).toBeFalsy();
                expect(b.isEnabled).toBeFalsy();
            });
    });

    it('DELETE /user', () => {
        return request(app.getHttpServer())
            .delete('/user/3')
            .auth(adminToken, { type: "bearer" })
            .expect(200);
    });

});
