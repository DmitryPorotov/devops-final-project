class Player {
    get token(): string | undefined {
        return this._token;
    }

    set token(value: string | undefined) {
        this._token = value;
    }
    get ws(): WebSocket | undefined {
        return this._ws;
    }

    set ws(value: WebSocket | undefined) {
        this._ws = value;
    }
    get id(): number {
        return this._id;
    }

    set id(value: number) {
        this._id = value;
    }
    private _id: number = -1;

    private _ws?: WebSocket;

    private _token?: string;

    private _messageCallback?: (m: string) => void;

    constructor(private _username: string, private _password: string) {
    }

    get username(): string {
        return this._username;
    }
    set username(v: string) {
        this._username = v;
    }

    get password(): string {
        return this._password;
    }
    set password(v:string) {
        this._password = v;
    }


}

export default Player;