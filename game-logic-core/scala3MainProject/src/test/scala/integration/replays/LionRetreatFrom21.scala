package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class LionRetreatFrom21 extends AnyFlatSpec with should.Matchers {
  "Lion" should "be able to retreat from 21 to 10 or to 23" in {
    val source = fromFile("saves/forIntegration/3--retreat_bug--2024-08-30T07-40-30.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)
    val message = MessageGameAction(-6, "3", ujson.Obj(
      "houseType" -> "lion",
      "actionType" -> "retreatUnitsAfterBattle",
      "targetTileNumber" -> 10
    ), null)

    val retVal = Reactor(message, ujson.Obj())
    assert(retVal.obj("action").str != "error")
  }
}
