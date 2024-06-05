import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import utils.{CreateGame, HttpUtils, JoinGame, PlayerBehavior}

class AllJoinSuite {
  @Test
  def allJoin(): Unit = {
    val owner1 = HttpUtils.login("a@b.com")
    val user2 = HttpUtils.login("b@b.com")
    val user3 = HttpUtils.login("admin@b.com")
    val user4 = HttpUtils.login("c@b.com")
    val user5 = HttpUtils.login("d@b.com")
    val user6 = HttpUtils.login("e@b.com")
    val o1Mb = CreateGame.getCreateGameOwnerMessagesBuilder(owner1)
    JoinGame.getPlayerJoinMessageBuilder(owner1, "wolf", Some(o1Mb))
    val u2Mb = JoinGame.getPlayerJoinMessageBuilder(user2, "lion")
    val u3Mb = JoinGame.getPlayerJoinMessageBuilder(user3, "kraken")
    val u4Mb = JoinGame.getPlayerJoinMessageBuilder(user4, "moose")
    val u5Mb = JoinGame.getPlayerJoinMessageBuilder(user5, "rose")
    val u6Mb = JoinGame.getPlayerJoinMessageBuilder(user6, "pufferfish")
    val o1Pb: PlayerBehavior = new PlayerBehavior(owner1, o1Mb.getMap) {
      override def onMessage(message: String): ujson.Obj =
        val j = super.onMessage(message)
          if j.obj.getOrElse("gameSettings", null) != null then
            allJoin(j)
        j

      @Test
      def allJoin(j: ujson.Obj): Unit =
        val p = j.obj("gameSettings").obj("players").arr
        if p.length >= 6 then
          Assertions.assertTrue(!j.obj("gameSettings").obj("gameUuid").isNull)
          j.obj("gameSettings").obj("gameUuid") = "uuid"
          Assertions.assertTrue(!j.obj("messageId").isNull)
          j.obj("messageId") = "uuid"
          j.obj("gameSettings").obj("players") = p.sortWith((a ,b) => a.obj("userId").num < b.obj("userId").num)
          try
            Approvals.verify(j.render(2))
          finally
            println("exiting")
            Thread.sleep(500L)
            System.exit(0)
    }
    val u2Bp = new PlayerBehavior(user2, u2Mb.getMap)
    val u3Bp = new PlayerBehavior(user3, u3Mb.getMap)
    val u4Bp = new PlayerBehavior(user4, u4Mb.getMap)
    val u5Bp = new PlayerBehavior(user5, u5Mb.getMap)
    val u6Bp = new PlayerBehavior(user6, u6Mb.getMap)
    o1Pb.connect()
    Thread.sleep(2000L)
    u2Bp.connect()
    u3Bp.connect()
    u4Bp.connect()
    u5Bp.connect()
    u6Bp.connect()
    Thread.sleep(10_0000L)
  }
}
