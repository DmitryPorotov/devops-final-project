package integration.replays

import fwc.communication.Reactor
import fwc.game.houses.HouseType.PufferFish
import fwc.game.phases.actionSubPhases.SubPhaseResolveMarchOrder
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class PufferFishMarchOrderDoesNotExist extends AnyFlatSpec with should.Matchers  {
  "NextOrderFinder" should "not switch to puffer fish since he has no more march orders" in {

    val source = fromFile("saves/forIntegration/3--pufferMarchOrderDoesNotExist--2024-09-04T01-23-02.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val state = Reactor.prepareShutdown("3").currentGameState

    assert(Reactor.prepareShutdown("3").currentGameState.subPhase.asInstanceOf[SubPhaseResolveMarchOrder].houseType != PufferFish)
  }
}
