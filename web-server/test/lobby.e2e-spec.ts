import {INestApplication} from "@nestjs/common";
import initApp, {getUserToken, getUser2Token} from "./init-app";
import * as request from "supertest";

describe('LobbiesController', () => {
    let app: INestApplication;
    let userToken: string;

    beforeAll(async () => {
        app = await initApp();
        userToken = await getUserToken(app);
    });

    afterAll(async () => {
        await app.close();
    });

    test('POST /lobby', () => {
        return request(app.getHttpServer())
            .post('/lobby')
            .auth(userToken, {type: "bearer"})
            .send({
                name: 'myLobby',
                password: '1234'
            })
            .expect(201)
            .expect(r => {
                const b = r.body;
                expect(b.name).toBe('myLobby');
                expect(typeof b.id).toBe('number');
            });
    });

    test('GET /lobby', () => {
        return request(app.getHttpServer())
            .get('/lobby')
            .auth(userToken, {type: "bearer"})
            .expect(200)
            .expect(r => {
                const b = r.body;
                expect(b.length).toBeGreaterThanOrEqual(2);
            });
    });

    test('PATCH /lobby/1', () => {
        return request(app.getHttpServer())
            .patch('/lobby/1')
            .auth(userToken, {type: "bearer"})
            .send({
                name: 'newName',
                password: '4321'
            })
            .expect(200)
            .expect(r => {
                const b = r.body;
                expect(b.name).toBe('newName');
                expect(b.password).toBeUndefined();
            });
    });

    test('PATCH /lobby/1/join', async () => {
        return request(app.getHttpServer())
            .patch('/lobby/1/join')
            .auth(await getUser2Token(app), {type: "bearer"})
            .send({
                password: '4321'
            })
            .expect(200)
            .expect(r => {
                const b = r.body;
                expect(b.participants.length).toBeGreaterThanOrEqual(2);
            });
    });

    test('PATCH /lobby/1/leave', async () => {
        return request(app.getHttpServer())
            .patch('/lobby/1/leave')
            .auth(await getUser2Token(app), {type: "bearer"})
            .send({
                password: '4321'
            })
            .expect(200)
            .expect(r => {
                const b = r.body;
                expect(b.participants.length).toBe(1);
            });
    });

});
