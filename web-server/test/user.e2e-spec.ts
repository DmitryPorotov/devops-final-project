import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import {User} from "../src/user/entities/user.entity";
import initApp, {getAdminToken, getUserToken} from './init-app';

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

    test('GET /user/1', async () => {
        return request(app.getHttpServer())
            .get('/user/1')
            .auth(adminToken, { type: "bearer" })
            .expect(200)
            .expect((r) => {
                expect(r.body.id).toBe(1);
                expect(r.body.name).toBe('user1');
            });
    });

    test('GET /user/8', () => {
        return request(app.getHttpServer())
            .get('/user/8')
            .auth(adminToken, { type: "bearer" })
            .expect(200)
            .expect(r => {
                expect(r.body).toBeFalsy();
            })
    });

    test('POST /user', () => {
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

    test('DELETE /user', () => {
        return request(app.getHttpServer())
            .delete('/user/8')
            .auth(adminToken, { type: "bearer" })
            .expect(200);
    });

    test('PATCH /user', async () => {
        const userToken = await getUserToken(app);
        return request(app.getHttpServer())
            .patch('/user')
            .auth(userToken, { type: "bearer" })
            .send({name: 'newUserName1'})
            .expect(200)
            .expect(r => {
                const b: User = r.body;
                expect(b.name).toBe('newUserName1');
            });
    });

});
