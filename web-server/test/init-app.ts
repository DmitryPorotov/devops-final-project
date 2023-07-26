import {Test} from "@nestjs/testing";
import {AppModule} from "../src/app.module";
import {FastifyAdapter, NestFastifyApplication} from "@nestjs/platform-fastify";
import * as request from "supertest";

export default async () => {
    const moduleRef = await Test.createTestingModule({
        imports: [AppModule],
    })
        .compile();
    const app = moduleRef.createNestApplication<NestFastifyApplication>(new FastifyAdapter());

    await app.init();
    await app.getHttpAdapter().getInstance().ready();
    return app;
}

export async function getAdminToken(app): Promise<string> {
    const response = await request(app.getHttpServer())
        .post('/auth/login')
        .send({
            email: 'admin@b.com',
            password: '12345678'
        });
    return response.body.token
}
