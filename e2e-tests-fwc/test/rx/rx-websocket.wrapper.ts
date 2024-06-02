import { Subject } from 'rxjs';
import { ChatMessageInterface, WorkerMessageInterface } from 'test/message.interface';
import { WebSocket } from 'ws';

export class RxWebsocketWrapper<T> {
    constructor(private url: string) {
    }

    private webSocket: WebSocket;

    /**
     * @throws Error
     */
    async init(): Promise<void> {
        return new Promise((resolve) => {
            this.webSocket = new WebSocket(this.url);
            this.subject$$ = new Subject<T>();
            this.webSocket.addEventListener('message', (event) => {
                try {
                    const data = JSON.parse(event.data.toString())
                    this.subject$$.next(data)
                } 
                catch (e) {
                    this.subject$$.error(e)
                }
            });
            this.webSocket.addEventListener('error', error => {
                this.subject$$.error(error)
            });
            this.webSocket.addEventListener('open', () => {
                resolve();
            });
        })
    }

    private subject$$ : Subject<T>;

    getSubject() {
        return this.subject$$;
    }

    send(message: ChatMessageInterface | WorkerMessageInterface) {
        this.webSocket.send(JSON.stringify(message))
    }
}