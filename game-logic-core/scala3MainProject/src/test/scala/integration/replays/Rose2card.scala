package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Rose2card extends AnyFlatSpec with should.Matchers  {
  "Rose 2 card" should "not cause a type cast exception" in {

    val source = fromFile("saves/forIntegration/3--rose2card--2024-08-31T03-04-35.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val state = Reactor.prepareShutdown("3").currentGameState
    val message = MessageGameAction(-5, "3", ujson.Obj(
      "houseType" -> "rose",
      "actionType" -> "chooseHouseCard",
      "cardCode" -> 2,
    ), null)
    Reactor(message, ujson.Obj())
    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseResolveHouseCard])
  }
}
