import {Test} from "@nestjs/testing";
import {AppModule} from "../src/app.module";
import {FastifyAdapter, NestFastifyApplication} from "@nestjs/platform-fastify";
import * as request from "supertest";
import { BadRequestException, INestApplication, ValidationPipe } from "@nestjs/common"
import {AuthCredentialsDto} from "../src/auth/dto/auth.credentials.dto";
import { WsAdapter } from "@nestjs/platform-ws"
import { ValidationError } from "@nestjs/common/interfaces/external/validation-error.interface"

export default async (): Promise<INestApplication> => {
    const moduleRef = await Test.createTestingModule({
        imports: [AppModule],
    })
        .compile();
    const app = moduleRef.createNestApplication<NestFastifyApplication>(new FastifyAdapter({logger: true}));
    await app.useWebSocketAdapter(new WsAdapter(app));
    app.enableCors({
        origin: '*'
    });
    app.useGlobalPipes(new ValidationPipe({
        whitelist: true,
        transform: true,
        exceptionFactory: (errors: ValidationError[]) => new BadRequestException(
            {
                message: errors.reduce((acc,cur) => {
                    acc[cur.property] = Object.values(cur.constraints);
                    return acc;
                }, {}),
                statusCode: 400
            }

        )}));

    await app.init();
    await app.getHttpAdapter().getInstance().ready();
    return app;
}

export const getAdminToken = async (app): Promise<string> => {
    return await doLogin(app, {
        email: 'admin@b.com',
        password: '12345678'
    });
};

export const getUserToken = async (app): Promise<string> => {
    return await doLogin(app, {
        email: 'a@b.com',
        password: '12345678'
    });
};

export const getUser2Token = async (app): Promise<string> => {
    return await doLogin(app, {
        email: 'b@b.com',
        password: '12345678'
    });
};

const doLogin = async (app, credentials: AuthCredentialsDto) => {
    const response = await request(app.getHttpServer())
        .post('/auth/login')
        .send(credentials);
    return response.body.token
};

