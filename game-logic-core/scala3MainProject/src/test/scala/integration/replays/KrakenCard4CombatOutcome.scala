package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class KrakenCard4CombatOutcome extends AnyFlatSpec with should.Matchers  {
  "Calculating combat outcome with kraken card number 4" should "not cause an infinite loop" in {

    val source = fromFile("saves/forIntegration/3--kraken4card--2024-08-27T09-14-00.json")
    val lines = try source.mkString finally source.close

    val message1 = MessageGameAction(-4, "3", ujson.Obj(
      "houseType" -> "kraken",
      "actionType" -> "chooseHouseCard",
      "cardCode" -> 4
    ), null)

    val message2 = MessageGameAction(-6, "3", ujson.Obj(
      "houseType" -> "lion",
      "actionType" -> "chooseHouseCard",
      "cardCode" -> 2
    ), null)

    val message3 = MessageGameAction(-4, "3", ujson.Obj(
      "houseType" -> "kraken",
      "actionType" -> "useValyrianSteelBlade",
      "choice" -> "nothing"
    ), null)

    Reactor.restoreGame(lines)
    val result1 = Reactor(message1, ujson.Obj())
    val result2 = Reactor(message2, ujson.Obj())
    val result3 = Reactor(message3, ujson.Obj())

    val gameState=Reactor.prepareShutdown("3").currentGameState
    assert(gameState.combat == null)
    assert(gameState.armies(15).head.isDefeated)

  }
}
