import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import initApp from './init-app';


describe('AppController', () => {
  let app: INestApplication;

  beforeAll(async () => {
    app = await initApp();
  });

  afterAll(async () => {
    await app.close();
  });

  test('/ (GET)', () => {
    return request(app.getHttpServer())
        .get('/')
        .expect(200)
        .expect('Hello World!');
  });

});
