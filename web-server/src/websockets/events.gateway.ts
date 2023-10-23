import {
    OnGatewayConnection,
    WebSocketGateway,
    WebSocketServer,
} from '@nestjs/websockets';
import { Server } from 'ws';
import {AuthGuard} from "../auth/auth.guard";
import {IncomingMessage} from "http";
import WebsocketService from "./websocket.service";
import { Logger, UnauthorizedException } from "@nestjs/common"
import WebsocketWithUserInterface from "./websocket-with-user.interface";



@WebSocketGateway({
    cors: {
        origin: '*',
    },
    path: '/'
})

export class EventsGateway implements OnGatewayConnection{
    private readonly logger = new Logger(EventsGateway.name);

    constructor(
        private authGuard: AuthGuard,
        private websocketService: WebsocketService
    ) {
    }
    @WebSocketServer()
    server: Server;

    async handleConnection(client: WebsocketWithUserInterface, message: IncomingMessage): Promise<any> {

        const [, token] = message.url.split('=');
        let user;
        try {
            user = await this.authGuard.getUser(token);
        }
        catch (e) {
            if (e instanceof UnauthorizedException) {
                client.close();
                return;
            }
            else throw e;
        }
        this.logger.debug(`User ${user.id} - ${user.email} connected to the websocket`);
        client.user = user;
        client.addListener('message', (data) => {
            const strMessage = data.toString();
            this.logger.debug(`User ${user.id} - ${user.email} sent: ${strMessage}`);
            try {
                this.websocketService.handleMessage(client, JSON.parse(strMessage));
            }
            catch (e) {
                if (e instanceof SyntaxError) {
                    if (e.message.includes('JSON')) {
                        client.send(JSON.stringify({
                            type: 'error',
                            body: {
                                type: 'error',
                                body: e.message
                            }
                        }))
                    }
                }
                else throw e;
            }
        });
        client.addListener("close", (id, data) => {
            this.logger.debug(`User ${user.id} - ${user.email} disconnected from the websocket`);
        })
    }
}
