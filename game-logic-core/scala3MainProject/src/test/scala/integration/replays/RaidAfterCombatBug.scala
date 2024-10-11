package integration.replays

import fwc.communication.Reactor
import fwc.game.GameState
import fwc.game.board.MilitaryUnitType
import fwc.game.phases.actionSubPhases.SubPhaseResolveRaidOrder
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class RaidAfterCombatBug extends AnyFlatSpec with should.Matchers  {
  "Resolving raid" should "not come after march" in {

    val source = fromFile("saves/forIntegration/3--raidAfterCombat--2024-10-08T12-11-52.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum, currentState, action, newState: GameState) =>{
      if newState.armies.exists(_._1 == 40) && newState.armies(40).exists(x => x.unitType == MilitaryUnitType.PowerToken)
      then
        val a = 0
    }))

    val gameState = Reactor.prepareShutdown("3").currentGameState
    assert(!gameState.subPhase.isInstanceOf[SubPhaseResolveRaidOrder])

  }
}
