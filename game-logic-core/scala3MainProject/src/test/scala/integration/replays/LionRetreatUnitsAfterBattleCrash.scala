package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.actionSubPhases.SubPhaseChooseHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class LionRetreatUnitsAfterBattleCrash extends AnyFlatSpec with should.Matchers  {
  "Lion retreat after battle order" should "not cause an exception" in {

    val source = fromFile("saves/forIntegration/3--lionRetreatUnitsAfterBattleCrash--2024-09-05T04-17-08.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val message = MessageGameAction(-6, "3", ujson.Obj(
      "houseType" -> "lion",
      "actionType" -> "retreatUnitsAfterBattle",
      "targetTileNumber" -> 10,
    ), null)

    val result = Reactor(message, ujson.Obj())
    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseChooseHouseCard])
  }
}
