import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import utils.{JoinGame, PlayerBehavior}

class AllJoinSuite {
  @Test
  def allJoin(): Unit = {
    val (users, mesBuilders) = JoinGame.getAllUsersAndMessageBuilders
    val o1Pb: PlayerBehavior = new PlayerBehavior(users(1), mesBuilders(1).getMap) {
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
    val u2Bp = new PlayerBehavior(users(2), mesBuilders(2).getMap)
    val u3Bp = new PlayerBehavior(users(3), mesBuilders(3).getMap)
    val u4Bp = new PlayerBehavior(users(4), mesBuilders(4).getMap)
    val u5Bp = new PlayerBehavior(users(5), mesBuilders(5).getMap)
    val u6Bp = new PlayerBehavior(users(6), mesBuilders(6).getMap)
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
