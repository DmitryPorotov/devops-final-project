import {WebSocket} from "ws";
import {LoginUserDto} from "../user/dto/login-user.dto";

interface WebsocketWithUserInterface extends WebSocket {
    user?: LoginUserDto;
}

export default WebsocketWithUserInterface;