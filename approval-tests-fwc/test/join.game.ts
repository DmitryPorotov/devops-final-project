import { login } from "./login"
import { makeMessageId, sleep } from "./utility";
import { RxWebsocketWrapper } from "./rx-websocket.wrapper";
import settings from "./settings"
import { SendableMessage, ReceivableMessage, WorkerMessageInterface } from "./message.interface";
import { filter, tap } from "rxjs";
import { RxWsWrapperAndStepAdder } from "./create.game";


export default async function joinGame({email, password, joinAs}) {
    return new Promise<RxWsWrapperAndStepAdder>(async (resolve, reject) => {
        const user = await login({email, password});
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
        const joinGameMsgId = makeMessageId()
        console.log(`in join ${joinGameMsgId}`)
        const joinGame$ = subj$$.pipe(
            filter((m: ReceivableMessage) => { 
                console.log('in filter')
                return m.messageId === joinGameMsgId
            }),
            tap(m => {
                console.log('in tap')
                // unsub()
                resolve({
                    wsWrapper: wrap,
                    addStep: adder,
                    user
                })
            })
        )
        const sub = joinGame$.subscribe();
        function unsub() {
            sub.unsubscribe()
        }
        await sleep(10);
        console.log('before send #' + subj$$.observers.length)
        wrap.send({
            type:"action",
            userId: user.id,
            lobbyId: 2,
            action: "join_game",
            messageId: joinGameMsgId,
            joinAs,
            name: user.name
        })
        console.log(wrap.getSubject() === subj$$)
        console.log('after send #' + subj$$.observers.length)

        
        function adder(prevMsgId: string, onMessage: (msg: SendableMessage) => void): void {
            const addedFunc$ = subj$$.pipe(
                filter((m:any) => m.messageId === prevMsgId),
                tap((m: WorkerMessageInterface) => {
                    console.log('in tap adder ' + m.messageId)
                    onMessage(m)
                })
            )
            addedFunc$.subscribe();
        }
    })
}