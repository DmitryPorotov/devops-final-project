package integration.replays

import fwc.communication.Reactor
import fwc.game.phases.actionSubPhases.SubPhaseCalculateCombatOutcome
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class BladeChangeTobCard extends AnyFlatSpec with should.Matchers  {
  "Changing TOB card by the blade owner" should "continue the battle" in {

    val source = fromFile("saves/forIntegration/3--bladeChangeTob--2024-08-27T12-37-59.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val gameState = Reactor.prepareShutdown("3").currentGameState
    assert(gameState.combat != null)
    assert(gameState.subPhase.isInstanceOf[SubPhaseCalculateCombatOutcome])

  }
}
