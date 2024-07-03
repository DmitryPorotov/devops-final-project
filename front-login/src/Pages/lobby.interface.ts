import User from "../user.interface";

export default interface Lobby {
    id: number;

    name: string;

    password?: string;

    owner: User;

    participants: User[];

    sendTo?: number[]
}