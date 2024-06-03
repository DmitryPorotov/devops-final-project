import { LoginUserDto, makeMessageId } from "./utility";
import createGame from "./create.game";
import joinGame from "./join.game";
import { ReceivableMessage } from "./message.interface";
import { RxWebsocketWrapper } from "./rx-websocket.wrapper";

export type stepAdderFunc = (prevMsgId: string, onMessage: (msg: ReceivableMessage) => void) => void

export interface Connections {
    wolfAddStep: stepAdderFunc,
    wolfWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    wolfUser: LoginUserDto,
    lionAddStep: stepAdderFunc,
    lionWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    lionUser: LoginUserDto,
    krakenAddStep: stepAdderFunc,
    krakenWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    krakenUser: LoginUserDto,
    mooseAddStep: stepAdderFunc,
    mooseWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    mooseUser: LoginUserDto,
    roseAddStep: stepAdderFunc,
    roseWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    roseUser: LoginUserDto,
    pufferfishAddStep: stepAdderFunc,
    pufferfishWsWrapper: RxWebsocketWrapper<ReceivableMessage>,
    pufferfishUser: LoginUserDto,
}

export default async function allJoinGame() {
    return new Promise<Connections>(async (resolve, reject) => {
        try {
            const {addStep: wolfAddStep, wsWrapper: wolfWsWrapper, user: wolfUser} = await createGame()
            const {addStep: lionAddStep, wsWrapper: lionWsWrapper, user: lionUser} = await joinGame({email: "b@b.com", password: "12345678", joinAs: "lion"})
            const {addStep: krakenAddStep, wsWrapper: krakenWsWrapper, user: krakenUser} = await joinGame({email: "admin@b.com", password: "12345678", joinAs: "kraken"})
            const {addStep: mooseAddStep, wsWrapper: mooseWsWrapper, user: mooseUser} = await joinGame({email: "c@b.com", password: "12345678", joinAs: "moose"})
            const {addStep: roseAddStep, wsWrapper: roseWsWrapper, user: roseUser} = await joinGame({email: "d@b.com", password: "12345678", joinAs: "rose"})
            const {addStep: pufferfishAddStep, wsWrapper: pufferfishWsWrapper, user: pufferfishUser} = await joinGame({email: "e@b.com", password: "12345678", joinAs: "pufferfish"})
            const wolfJoinGameMsgId = makeMessageId()
            wolfWsWrapper.send({
                type:"action",
                userId: wolfUser.id,
                lobbyId: 2,
                action: "join_game",
                messageId: wolfJoinGameMsgId,
                joinAs: 'wolf',
                name: wolfUser.name
            })
            wolfAddStep(wolfJoinGameMsgId, function() {
                resolve({
                    wolfAddStep,
                    wolfWsWrapper,
                    wolfUser,
                    lionAddStep,
                    lionWsWrapper,
                    lionUser,
                    krakenAddStep,
                    krakenWsWrapper,
                    krakenUser,
                    mooseAddStep,
                    mooseWsWrapper,
                    mooseUser,
                    roseAddStep,
                    roseWsWrapper,
                    roseUser,
                    pufferfishAddStep,
                    pufferfishWsWrapper,
                    pufferfishUser,
                })
            })
        }
        catch (e) {
            reject(e)
        }
    })
}