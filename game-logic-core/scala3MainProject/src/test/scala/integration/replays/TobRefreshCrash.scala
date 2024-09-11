package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class TobRefreshCrash extends AnyFlatSpec with should.Matchers  {
  "Refreshing tides of battle deck" should "not crash" in {

    val source = fromFile("saves/forIntegration/3--tobRefreshCrash--2024-09-10T02-31-27.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines)

    val state = Reactor.prepareShutdown("3")

    val message = MessageGameAction(-5, "3",
      ujson.Obj(
        "houseType" -> "rose",
        "actionType" -> "chooseHouseCard",
        "cardCode" -> 1
      ),
      null)
    val result = Reactor(message, ujson.Obj())
    assert(result("reply").arr(2).obj("player_action").obj("actionType").str == "refreshTidesOfBattleDeck")
  }
}
