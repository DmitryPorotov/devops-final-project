import { Subject } from 'rxjs';
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
            WebSocket.Sender
            this.webSocket = new WebSocket(this.url);
            this.subject$$ = new Subject<T>();
            // this.webSocket.addEventListener('message', (event) => {
            //     try {
            //         const data = JSON.parse(event.data.toString())
            //         console.log('in on message ' + data.messageId + ' observers # ' + this.subject$$.observers.length)
            //         console.log(this.url)
            //         this.subject$$.next(data)
            //     } 
            //     catch (e) {
            //         this.subject$$.error(e)
            //     }
            // });
            // this.webSocket.addEventListener('error', error => {
            //     this.subject$$.error(error)
            // });
            // this.webSocket.addEventListener('open', () => {
            //     resolve();
            // });
            this.webSocket.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data.toString())
                    console.log('in on message ' + data.messageId + ' observers # ' + this.subject$$.observers.length)
                    console.log(this.url)
                    this.subject$$.next(data)
                } 
                catch (e) {
                    this.subject$$.error(e)
                }
            };
            this.webSocket.addEventListener('error', error => {
                this.subject$$.error(error)
            });
            this.webSocket.addEventListener('open', () => {
                resolve();
            });
        })
    }
    ///@ts-ignore
    private subject$$ : Subject<T>;

    getSubject() {
        return this.subject$$;
    }

    send(message: T) {
        console.log(this.url)
        this.webSocket.send(JSON.stringify(message))
    }
}