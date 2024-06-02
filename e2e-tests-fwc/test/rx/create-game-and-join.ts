import { login } from "../login"
import { makeMessageId } from "../utility";
import { RxWebsocketWrapper } from "./rx-websocket.wrapper";
import settings from "../settings"
import { ChatMessageInterface, ErrorMessageInterface, WorkerMessageInterface } from "test/message.interface";
import { filter, tap } from "rxjs";

type SendableMessage = ChatMessageInterface | WorkerMessageInterface

type ReceivableMessage = SendableMessage | ErrorMessageInterface

export interface RxWsWrapperAndStepAdder{
    wsWrapper: RxWebsocketWrapper<ReceivableMessage>
    addStep: (prevMsgId: string, onMessage: (msg: ReceivableMessage) => void) => void
}

export default async function createGameAndJoin() {
    return new Promise<RxWsWrapperAndStepAdder>(async (resolve, reject) => {
        const user = await login({email : "a@b.com", password: '12345678'});
        const wrap = new RxWebsocketWrapper<ReceivableMessage>(`ws://${settings.host}:${settings.port}${settings.wsPath}?_token=${user.token}`);
        await wrap.init();
        
        const subj$$ = wrap.getSubject();
        subj$$.subscribe({
            error: (e) => reject(e)
        })
        const err$ = subj$$.pipe(
            filter((m: ErrorMessageInterface) => {
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
                // expect((m as ChatMessageInterface).body.type).toBe('create')
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
        createChat$.subscribe();

        wrap.send({
            type: 'chat',
            userId: user.id,
            messageId: chatCreateMsgId,
            lobbyId: 2,
            body: {
                type: "create"
            }
        });
        
        const joinGameMsgId = makeMessageId();
        const createGame$ = subj$$.pipe(
            filter((m: WorkerMessageInterface) => 
                m.messageId === gameCreateMsgId
            ),
            tap((m: WorkerMessageInterface) => {
                // expect(m.gameId).toBe("2")
                wrap.send({
                    type:"action",
                    userId: user.id,
                    lobbyId: 2,
                    action: "join_game",
                    messageId: joinGameMsgId,
                    joinAs: 'wolf',
                    name: user.name
                })
            })
        )
        createGame$.subscribe();

        function adder(prevMsgId: string, onMessage: (msg: SendableMessage) => void): void {
            console.log('adder called')
            const addedFunc$ = subj$$.pipe(
                filter((m:WorkerMessageInterface) => m.messageId === prevMsgId),
                tap((m: WorkerMessageInterface) => {
                    console.log('in added function')
                    onMessage(m)
                })
            )
            addedFunc$.subscribe();
        }

        const joinGame$ = subj$$.pipe(
            filter((m: WorkerMessageInterface) => m.messageId === joinGameMsgId),
            tap((m: WorkerMessageInterface) => {
                resolve({
                    wsWrapper: wrap,
                    addStep: adder
                })
            })
        )
        joinGame$.subscribe();


    })
}