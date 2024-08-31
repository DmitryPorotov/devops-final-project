package integration.replays

import fwc.communication.Reactor
import fwc.game.phases.actionSubPhases.SubPhaseChooseHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class AfterCombatSwitchesToChooseCard extends AnyFlatSpec with should.Matchers  {
  "After combat" should "not switch to choose card" in {

    val source = fromFile("saves/forIntegration/3--afterCombatSwitchesToChooseCard--2024-08-31T07-50-40.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseChooseHouseCard])
  }
}
