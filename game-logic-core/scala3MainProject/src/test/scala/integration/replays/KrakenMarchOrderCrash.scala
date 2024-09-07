package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.actionSubPhases.SubPhaseChooseHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class KrakenMarchOrderCrash extends AnyFlatSpec with should.Matchers  {
  "Kraken march order" should "not cause an exception" in {

    val source = fromFile("saves/forIntegration/3--krakenMarchOrderCrash--2024-09-05T03-37-44.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val message = MessageGameAction(-4, "3", ujson.Obj(
      "houseType" -> "kraken",
      "actionType" -> "resolveMarchOrder",
      "sourceTileNumber" -> 15,
      "targets" -> ujson.Null
    ), null)

    val result = Reactor(message, ujson.Obj())
    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseChooseHouseCard])
  }
}
