package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.board.MilitaryUnitType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class WildlingsMusterFailsAfterDeck1MusterEvent extends AnyFlatSpec with should.Matchers  {
  "Moose" should "should be able to muster a knight at wildlings card phase after mustering phase of deck 1" in {

    val source = fromFile("saves/forIntegration/3--wildlingsMusterFailsAfterDeck1MusterEvent--2024-09-08T12-17-04.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines)

    val message = MessageGameAction(-2, "3", ujson.Obj(
      "houseType" -> "moose",
      "actionType" -> "wildlingsMusterAtCastle",
      "sourceTile" -> 46,
      "targetUnits" -> ujson.Arr(ujson.Arr(46, true, ujson.Obj(
        "house" -> "moose",
        "type" -> "knights",
      )))
    ), null)
    val result = Reactor(message, ujson.Obj())
    assert(result("action").str != "error")
    assert(Reactor.prepareShutdown("3").currentGameState.armies(46).exists(_.unitType == MilitaryUnitType.Knights))
  }
}
