package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Round5Deserialize extends AnyFlatSpec with should.Matchers {
  "The game" should "be able to deserialize this json, maybe" in {
    val source = fromFile("saves/forIntegration/3--round5--2024-09-09T02-53-20.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines)
//    val message = MessageGameAction(-6, "3", ujson.Obj(
//      "houseType" -> "lion",
//      "actionType" -> "retreatUnitsAfterBattle",
//      "targetTileNumber" -> 10
//    ), null)

//    val retVal = Reactor(message, ujson.Obj())
    assert(true)
  }
}
