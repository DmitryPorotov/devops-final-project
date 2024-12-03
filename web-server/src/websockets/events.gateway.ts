import {
  OnGatewayConnection,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server } from 'ws';
import { AuthGuard } from '../auth/auth.guard';
import { IncomingMessage } from 'http';
import WebsocketService from './websocket.service';
import { Logger, UnauthorizedException } from '@nestjs/common';
import WebsocketWithUserInterface from './websocket-with-user.interface';
import constants from '../constants';
import { sleep } from '../common/utilities';
import SystemMessageService from './system-message.service';
import { env } from 'process';
import NoWorkersException from '../redis/NoWorkersException';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
  path: '/ws',
})
export class EventsGateway implements OnGatewayConnection {
  private readonly logger = new Logger(EventsGateway.name);
  private readonly doPingPong: boolean;

  constructor(
    private authGuard: AuthGuard,
    private websocketService: WebsocketService,
    private systemMessageService: SystemMessageService,
  ) {
    this.doPingPong = env.DO_PING_PONG === 'true';
  }

  @WebSocketServer()
  server: Server;

  async handleConnection(
    client: WebsocketWithUserInterface,
    message: IncomingMessage,
  ): Promise<any> {
    const [, token] = message.url.split('=');
    let user;
    try {
      user = await this.authGuard.getUser(token);
    } catch (e) {
      if (e instanceof UnauthorizedException) {
        client.close();
        return;
      } else throw e;
    }
    this.logger.debug(
      `User ${user.id} - ${user.email} connected to the websocket`,
    );
    client.user = user;
    client.addListener('message', async (data) => {
      const strMessage = data.toString();
      this.logger.debug(`User ${user.id} - ${user.email} sent: ${strMessage}`);
      try {
        await this.websocketService.handleMessage(
          client,
          JSON.parse(strMessage),
        );
      } catch (e) {
        this.logger.debug('in catch', e);
        if (e instanceof SyntaxError) {
          if (e.message.includes('JSON')) {
            client.send(
              JSON.stringify({
                type: 'error',
                body: {
                  type: 'error',
                  body: e.message,
                },
              }),
            );
          }
        } else if (e instanceof NoWorkersException) {
          this.logger.error(e);
        } else throw e;
      }
    });
    client.addListener('close', (id, data) => {
      this.logger.debug(
        `User ${user.id} - ${user.email} disconnected from the websocket`,
      );
      client.pingInterval && clearInterval(client.pingInterval);
      this.systemMessageService
        .relayToLobbies(client, {
          type: 'system',
          userId: client.user.id,
          messageId: String(Math.random()),
          time: new Date().toISOString(),
          body: {
            type: 'error',
            body: `Player ${client.user.name} id ${client.user.id} has closed connection.`,
          },
        })
        .then();
    });
    client.addListener('error', (err) => {
      this.logger.debug(
        `User ${user.id} - ${user.email} had a connection error on the websocket. ${err.name}: ${err.message}`,
      );
    });
    if (this.doPingPong) {
      const pingPongHandler = () => (client.lastPong = new Date().getTime());
      client.addListener('pong', pingPongHandler);
      client.addListener('ping', pingPongHandler);
      const pingFunc = this.makePingFunc(client);
      pingFunc().then();
      client.pingInterval = setInterval(
        pingFunc,
        constants.WS_PING_INTERVAL,
      ) as unknown as number;
    }
  }

  private makePingFunc(
    client: WebsocketWithUserInterface,
  ): () => Promise<void> {
    return async () => {
      const now = new Date().getTime();
      client.ping();
      await sleep(5000);
      this.logger.debug(
        `after ping, user id ${client.user.id} delay ${client.lastPong - now}`,
      );
      if (!client.lastPong || client.lastPong - now > 3000) {
        await this.systemMessageService.relayToLobbies(client, {
          type: 'system',
          userId: client.user.id,
          messageId: String(Math.random()),
          time: new Date().toISOString(),
          body: {
            type: 'error',
            body: `Player ${client.user.name} id ${client.user.id} is timing out.`,
          },
        });
      } else {
        await this.systemMessageService.relayToLobbies(client, {
          type: 'system',
          userId: client.user.id,
          messageId: String(Math.random()),
          time: new Date().toISOString(),
          body: {
            type: 'ping',
            body: `${client.lastPong - now}`,
          },
        });
      }
    };
  }
}
