import { login } from "./login"
import { LoginUserDto, makeMessageId } from "./utility";
import { RxWebsocketWrapper } from "./rx-websocket.wrapper";
import settings from "./settings"
import { SendableMessage, ReceivableMessage, WorkerMessageInterface } from "./message.interface";
import { filter, tap } from "rxjs";


export interface RxWsWrapperAndStepAdder{
    wsWrapper: RxWebsocketWrapper<ReceivableMessage>
    addStep: (prevMsgId: string, onMessage: (msg: ReceivableMessage) => void) => void,
    user: LoginUserDto
}

export default async function createGame() {
    return new Promise<RxWsWrapperAndStepAdder>(async (resolve, reject) => {
        const user = await login({email : "a@b.com", password: '12345678'});
        const wrap = new RxWebsocketWrapper<ReceivableMessage>(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${user.token}`);
        await wrap.init();
        
        const subj$$ = wrap.getSubject();
        subj$$.subscribe({
            error: (e) => reject(e)
        })
        const err$ = subj$$.pipe(
            filter((m: ReceivableMessage) => {
                return m.action === 'error'
            }),
            tap(m => reject(m))
        )
        err$.subscribe();

        const chatCreateMsgId = makeMessageId()
        const gameCreateMsgId = makeMessageId()
        const createChat$ = subj$$.pipe(
            filter(m => m.messageId === chatCreateMsgId),
            tap(m => {
                createChatUnsub()
                wrap.send({
                    type: 'action',
                    userId: user.id,
                    messageId: gameCreateMsgId,
                    lobbyId: 2,
                    action: 'create_game',
                    isRandomHouses: false,
                })
            })
        )
        const createChatSub = createChat$.subscribe();
        function createChatUnsub() {
            createChatSub.unsubscribe()
        }
        wrap.send({
            type: 'chat',
            userId: user.id,
            messageId: chatCreateMsgId,
            lobbyId: 2,
            body: {
                type: "create"
            }
        });
        
        const createGame$ = subj$$.pipe(
            filter((m: any) => 
                m.messageId === gameCreateMsgId
            ),
            tap((m: WorkerMessageInterface) => {
                createGameUnsub()
                resolve({
                    wsWrapper: wrap,
                    addStep: adder,
                    user
                })
            })
        )
        const createGameSub = createGame$.subscribe();
        function createGameUnsub() {
            createGameSub.unsubscribe()
        }
        function adder(prevMsgId: string, onMessage: (msg: SendableMessage) => void): void {
            const addedFunc$ = subj$$.pipe(
                filter((m:any) => m.messageId === prevMsgId),
                tap((m: WorkerMessageInterface) => {
                    onMessage(m)
                })
            )
            addedFunc$.subscribe();
        }
    })
}