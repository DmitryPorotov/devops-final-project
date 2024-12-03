import { NestFactory } from '@nestjs/core';
import { env } from 'process';
import { BadRequestException, ValidationPipe } from '@nestjs/common';
import {
  FastifyAdapter,
  NestFastifyApplication,
} from '@nestjs/platform-fastify';
import { WsAdapter } from '@nestjs/platform-ws';
import { AppModule } from './app.module';
import { ValidationError } from '@nestjs/common/interfaces/external/validation-error.interface';

async function bootstrap() {
  const app = await NestFactory.create<NestFastifyApplication>(
    AppModule,
    new FastifyAdapter({ logger: true }),
  );
  await app.setGlobalPrefix('api/v1');
  await app.useWebSocketAdapter(new WsAdapter(app));
  app.enableCors({
    origin: '*',
  });
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      exceptionFactory: (errors: ValidationError[]) =>
        new BadRequestException({
          message: errors.reduce((acc, cur) => {
            acc[cur.property] = Object.values(cur.constraints);
            return acc;
          }, {}),
          statusCode: 400,
        }),
    }),
  );
  await app.listen(env.SERVER_PORT, '0.0.0.0');
}

bootstrap();
