package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class WildlingsReurnDiscardedCardBug extends AnyFlatSpec with should.Matchers  {
  "Wolf" should "should be able to return discarded card 1" in {

    val source = fromFile("saves/forIntegration/3--wildlingsReurnDiscardedCardBug--2024-09-08T11-54-27.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val message = MessageGameAction(-1, "3", ujson.Obj(
      "houseType" -> "wolf",
      "actionType" -> "wildlingsReturnHouseCard",
      "cardCode" -> 1
    ), null)

    val result = Reactor(message, ujson.Obj())
    assert(result("action").str != "error")
    assert(Reactor.prepareShutdown("3").currentGameState.discardedHouseCards(HouseType.Wolf).isEmpty)
  }
}
