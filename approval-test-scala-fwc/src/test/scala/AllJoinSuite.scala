import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import ujson.Obj
import utils.{JoinGame, PlayerBehavior, TestRunner}

class AllJoinSuite {
  @Test
  def allJoin(): Unit = {
    val (users, mesBuilders) = JoinGame.getAllUsersAndMessageBuilders
    mesBuilders(1).addOne(key = "default", testRunner = Some(new TestRunner {
      override def onMessage(jsonMessage: Obj): Unit =
        if jsonMessage.obj.getOrElse("gameSettings", null) != null then
          allJoin(jsonMessage)

      @Test
      def allJoin(j: ujson.Obj): Unit =
        val p = j.obj("gameSettings").obj("players").arr
        println("# players " + p.length)
        if p.length >= 6 then
          Assertions.assertTrue(!j.obj("gameSettings").obj("gameUuid").isNull)
          j.obj("gameSettings").obj("gameUuid") = "uuid"
          Assertions.assertTrue(!j.obj("messageId").isNull)
          j.obj("messageId") = "uuid"
          j.obj("gameSettings").obj("players") = p.sortWith((a, b) => a.obj("userId").num < b.obj("userId").num)
          try
            Approvals.verify(j.render(2))
          finally
            println("exiting")
            Thread.sleep(500L)
            System.exit(0)
    }))
    val o1Pb = new PlayerBehavior(users(1), mesBuilders(1).getMap)
    val u2Pb = new PlayerBehavior(users(2), mesBuilders(2).getMap)
    val u3Pb = new PlayerBehavior(users(3), mesBuilders(3).getMap)
    val u4Pb = new PlayerBehavior(users(4), mesBuilders(4).getMap)
    val u5Pb = new PlayerBehavior(users(5), mesBuilders(5).getMap)
    val u6Pb = new PlayerBehavior(users(6), mesBuilders(6).getMap)
    o1Pb.connect()
    Thread.sleep(2000L)
    u2Pb.connect()
    u3Pb.connect()
    u4Pb.connect()
    u5Pb.connect()
    u6Pb.connect()
    Thread.sleep(100_000L)
  }
}
