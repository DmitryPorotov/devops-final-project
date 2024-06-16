package inputOnly

import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import ujson.Obj
import utils.{PlayerInputtingMessagesBuilder, TestRunner}

class GameLoadInputtingOnlySuite {
  @Test
  def loadGame(): Unit =
    val mb = PlayerInputtingMessagesBuilder.init().copy(action = Some("load"))
    mb.addOne(Some(ujson.Obj(
      "lobbyId" -> 2,
      "userId" -> 1,
      "action" -> "load",
      "type" -> "action",
      "saveName" -> "2--pufferAttackMoose--2024-05-12T12-42-53.json"
    )), Some(new TestRunner {
      override def onMessage(jsonMessage: Obj): Unit =
        gameLoaded(jsonMessage)

      @Test
      def gameLoaded(json: ujson.Obj): Unit =
        Assertions.assertTrue(json.obj("gameId").str.equals("2"))
    }))

    val mb2 = mb.copy(action = Some("get_game_state"))
    mb2.addOne(Some(ujson.Obj()), Some(new TestRunner {
      override def onMessage(jsonMessage: Obj): Unit =
        loadGameMooseAttackedByPufferFish(jsonMessage)

      @Test
      def loadGameMooseAttackedByPufferFish(j: Obj): Unit =
        j.obj("messageId") = "uuid"
        try
          Approvals.verify(j.render(2))
        catch
          case e =>
            PlayerInputtingMessagesBuilder.endTest()
            throw e
    }))

    val mb3 = mb2.copy(action = Some("game_action"))
    mb3.addOne(Some(ujson.Obj(
      "player_action" -> ujson.Obj(
        "actionType" -> "chooseHouseCard",
        "cardCode" -> 0,
        "houseType" -> "moose",
      )
    )), Some(new TestRunner {
      private var replyNumber = 0
      private var replies: Seq[ujson.Obj] = Seq()

      override def onMessage(jsonMessage: Obj): Unit =
        mooseLosesToPufferfish(jsonMessage)


      @Test
      def mooseLosesToPufferfish(j: Obj): Unit =
        j.obj("messageId") = "uuid"
        replies = replies :+ j
        replyNumber += 1
        if replyNumber >= 8 then
          try
            val str = ujson.Arr.from(replies).render(2)
            Approvals.verify(str)
          catch
            case e =>
              PlayerInputtingMessagesBuilder.endTest()
              throw e
          finally
            PlayerInputtingMessagesBuilder.endTest()
    }))


    PlayerInputtingMessagesBuilder.startTest(mb3)
}
