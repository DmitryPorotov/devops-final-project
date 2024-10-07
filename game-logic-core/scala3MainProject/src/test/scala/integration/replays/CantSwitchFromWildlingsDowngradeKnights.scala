package integration.replays

import fwc.communication.Reactor
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveSupportOrder
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsDowngradeKnights
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class CantSwitchFromWildlingsDowngradeKnights extends AnyFlatSpec with should.Matchers {
  "The game" should "switch to next phase when everyone downgraded their knights" in {
    val source = fromFile("saves/forIntegration/3--cantSwitchFromWildlingsDowngradeKnights--2024-10-07T01-49-35.json")
    val lines = try source.mkString finally source.close


    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
      if actNum >= 98
      then
        val a = 0
    }))

    val game = Reactor.prepareShutdown("3").currentGameState
    assert(!game.subPhase.isInstanceOf[SubPhaseWildlingsDowngradeKnights])
  }
}
