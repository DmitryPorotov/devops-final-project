package bots

import org.approvaltests.Approvals
import org.junit.jupiter.api.{Assertions, Test}
import ujson.Obj
import utils.{HttpUtils, JoinGame, MessagesBuilder, PlayerBehavior, TestRunner}

class BotsPlaceRandomOrders {
  //note: it looks like I can't use this system to test bots because I don't control messageIds coming from bots
  @Test
  def createWithBots(): Unit = {
    val owner1 = HttpUtils.login("a@b.com")
    val mb = MessagesBuilder(owner1.id, 3)
    mb.addOne(Some(ujson.Obj(
      "body" -> ujson.Obj(
        "type" -> "create"
      ))
    ))
    val mb2 = mb.copy(action = Some("create_game"), messageType = "action")
    mb2.addOne(Some(ujson.Obj(
      "isRandomHouses" -> false
    )))
    val mb3 = JoinGame.getPlayerJoinMessageBuilder(owner1, "kraken", Some(mb2))
    val mb4 = mb3.copy(action = Some("fill_with_bots"))

    mb4.addOne(Some(Obj(
      "player_action" -> Obj(
        "houseTypes" -> ujson.Arr(
        "moose", "lion", "wolf", "rose", "pufferfish"
      )
      )
    )))
    val mb5 = mb4.copy(action = Some("game_action"))
    mb5.addOne()
    val pb = new PlayerBehavior(owner1, mb5.getMap)
    pb.connect()
    Thread.sleep(10000L)
  }
}
