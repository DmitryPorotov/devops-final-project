import { makeMessageId } from "../utility";
import { WorkerMessageInterface } from "../message.interface";
import createGameAndJoin from './create-game-and-join'

describe("rx_add_order", function() {

    test('add_order', function() {
        return new Promise<void>(async (resolve, reject) => {
            try {

                const addOrderMsgId = makeMessageId()

                const {wsWrapper, addStep} = await createGameAndJoin()
                wsWrapper.send({
                    "player_action":
                    {
                        "houseType":"wolf",
                        "order":{
                            "type":"march"
                        },
                        "actionType":"addOrder",
                        "tileNumber": 3
                    },
                    "action":"game_action",
                    messageId: addOrderMsgId,
                    "type": "action",
                    "lobbyId":2,
                    "userId":1
                } as WorkerMessageInterface)
                addStep(
                    addOrderMsgId,
                    function(msg: WorkerMessageInterface) {
                        expect(msg.reply).toBeDefined()
                        resolve()
                    }
                )

            }
            catch (e) {
                reject(e)
            }
        })
    });
})