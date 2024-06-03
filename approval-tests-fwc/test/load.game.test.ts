import { beforeAll, describe, test } from "@jest/globals";
import allJoinGame from "./all.join.game";
import {configure} from "approvals/lib/config";
import {JestReporter} from "approvals/lib/Providers/Jest/JestReporter";
import { makeMessageId } from "./utility";
import { ReceivableMessage } from "./message.interface";
import { verifyAsJson } from "approvals/lib/Providers/Jest/JestApprovals";


describe("load_game", () => {
  beforeAll(() => {
    configure({
      reporters: [new JestReporter()],
    });
  });
   test("lion_loads_game", function() {
        return new Promise<void>(async () => {
            const cons = await allJoinGame()
            const loadGameMsgId = makeMessageId()
            cons.lionWsWrapper.send({
                userId: cons.lionUser.id,
                lobbyId: 2,
                messageId: loadGameMsgId,
                type: "action",
                action: 'load',
                saveName: "2--name--2024-03-05T11-46-59.json"
            })
            cons.lionAddStep(loadGameMsgId, function(msg: ReceivableMessage) {
                verifyAsJson(msg)
            })
        })

   });

});
