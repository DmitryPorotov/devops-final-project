package singleThreaded

import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import ujson.Obj
import utils.{SingleThreadedMessagesBuilder, TestRunner}

class GameLoadSuiteSt {
  @Test
  def loadGame(): Unit = {
    val messageBuilders = SingleThreadedMessagesBuilder.init()
    messageBuilders(3) = messageBuilders(3).copy(action = Some("load"))
    messageBuilders(3).addOne(Some(ujson.Obj(
      "lobbyId" -> 2,
      "userId" -> 3,
      "action" -> "load",
      "type" -> "action",
      "saveName" -> "2--name--2024-05-12T12-42-53.json"
    )), Some(new TestRunner {
      override def onMessage(jsonMessage: Obj): Unit =
        gameLoaded(jsonMessage)

      @Test
      def gameLoaded(json: ujson.Obj): Unit =
        Assertions.assertTrue(json.obj("gameId").str.equals("2"))
    }))

    messageBuilders(3) = messageBuilders(3).copy(action = Some("get_game_state"))
    messageBuilders(3).addOne(Some(ujson.Obj()), Some(new TestRunner {
      override def onMessage(jsonMessage: Obj): Unit =
        loadGameMooseAttackedByPufferFish(jsonMessage)

      @Test
      def loadGameMooseAttackedByPufferFish(j: Obj): Unit =
        j.obj("messageId") = "uuid"
        try
         Approvals.verify(j.render(2))
        catch
          case e =>
            SingleThreadedMessagesBuilder.endTest()
            throw e
    }))


    messageBuilders(3) = messageBuilders(3).copy(action = Some("game_action"))
    messageBuilders(3).addOne(Some(ujson.Obj(
      "player_action" -> ujson.Obj(
        "actionType" -> "chooseHouseCard",
        "cardCode" -> 0
      )
    )), Some(new TestRunner{
      private var replyNumber = 0
      private var replies: Seq[ujson.Obj]  = Seq()
      override def onMessage(jsonMessage: Obj): Unit =
        mooseLosesToPufferfish(jsonMessage)


      @Test
      def mooseLosesToPufferfish(j: Obj): Unit =
        j.obj("messageId") = "uuid"
        replies = replies :+ j
        replyNumber += 1
        if replyNumber >= 8 then
          try
            Approvals.verify(ujson.Arr.from(replies).render(2))
          catch
            case e =>
              SingleThreadedMessagesBuilder.endTest()
              throw e
          finally
            SingleThreadedMessagesBuilder.endTest()
    }))

    messageBuilders(3).addOne()

    Assertions.assertTrue(SingleThreadedMessagesBuilder.testMessages())

    SingleThreadedMessagesBuilder.startTest()
  }
}
