package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Moose2card extends AnyFlatSpec with should.Matchers  {
  "Moose" should "be able to upgrade a footman on defended territory" in {

    val source = fromFile("saves/forIntegration/3--moose2card--2024-08-31T09-06-49.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val state = Reactor.prepareShutdown("3").currentGameState
    val message = MessageGameAction(-2, "3", ujson.Obj(
      "houseType" -> "moose",
      "actionType" -> "resolveCardMoose2",
      "tileNumber" -> 45,
    ), null)
    Reactor(message, ujson.Obj())
    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseResolveHouseCard])
  }
}
