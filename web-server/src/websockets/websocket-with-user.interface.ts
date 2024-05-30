import {WebSocket} from "ws";
import {LoginUserDto} from "../user/dto/login-user.dto";
import {Observable} from "rxjs";
import {MessageInterface} from "./messages/message.interface";
import {WorkerMessageInterface} from "./messages/worker-message.interface";

interface WebsocketWithUserInterface extends WebSocket {
    user?: LoginUserDto;
    messageObs?: Observable<MessageInterface | WorkerMessageInterface>
    pingInterval?: number;
    lastPong?: number;
}

export default WebsocketWithUserInterface;