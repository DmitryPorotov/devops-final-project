package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Moose0card extends AnyFlatSpec with should.Matchers  {
  "Using Moose card 0" should "not crash the game" in {

    val source = fromFile("saves/forIntegration/3--mooseCard0--2024-09-03T12-32-26.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val state = Reactor.prepareShutdown("3").currentGameState
    val message = MessageGameAction(-2, "3", ujson.Obj(
      "houseType" -> "moose",
      "actionType" -> "chooseHouseCard",
      "cardCode" -> 0
    ), null)
    Reactor(message, ujson.Obj())
    val phase = Reactor.prepareShutdown("3").currentGameState.subPhase
    assert(phase.isInstanceOf[SubPhaseAddOrder])
  }
}
