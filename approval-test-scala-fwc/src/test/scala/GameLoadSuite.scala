import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import ujson.Obj
import utils.{JoinGame, PlayerBehavior, TestRunner}


class GameLoadSuite {
  @Test
  def loadGame(): Unit = {
    val (users, messageBuilders) = JoinGame.getAllUsersAndMessageBuilders()
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
        Approvals.verify(j.render(2))
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
          Approvals.verify(ujson.Arr.from(replies).render(2))
    }))

    messageBuilders(3).addOne()

    val u1Pb = new PlayerBehavior(users(1), messageBuilders(1).getMap)
    val u2Pb = new PlayerBehavior(users(2), messageBuilders(2).getMap)
    val u3Pb = new PlayerBehavior(users(3), messageBuilders(3).getMap)
    val u4Pb = new PlayerBehavior(users(4), messageBuilders(4).getMap)
    val u5Pb = new PlayerBehavior(users(5), messageBuilders(5).getMap)
    val u6Pb = new PlayerBehavior(users(6), messageBuilders(6).getMap)

    u1Pb.connect()
    Thread.sleep(2000L)
    u2Pb.connect()
    u3Pb.connect()
    u4Pb.connect()
    u5Pb.connect()
    u6Pb.connect()
    Thread.sleep(1000L)
  }
}
