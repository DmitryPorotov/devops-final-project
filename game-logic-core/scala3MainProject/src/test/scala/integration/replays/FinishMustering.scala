package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.roundEventsSubPhases.SubPhaseTracksBids
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class FinishMustering  extends AnyFlatSpec with should.Matchers  {
  "Finish mustering" should "switch to next player" in {

    val source = fromFile("saves/forIntegration/3--finishMustering--2024-09-03T11-48-10.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val message = MessageGameAction(-5, "3", ujson.Obj(
      "houseType"-> "rose",
      "actionType"-> "finishMustering"
    ), null)
    Reactor(message, ujson.Obj())
    val phase = Reactor.prepareShutdown("3").currentGameState.subPhase
    assert(phase.isInstanceOf[SubPhaseTracksBids])
  }
}
