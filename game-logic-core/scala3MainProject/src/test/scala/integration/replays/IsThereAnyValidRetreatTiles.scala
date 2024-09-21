package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.actions.action.ActionCalculateCombatOutcome
import fwc.game.phases.actionSubPhases.SubPhaseCleanUpAfterCombat
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class IsThereAnyValidRetreatTiles extends AnyFlatSpec with should.Matchers {
  "The game" should "not offer retreating if there is not a valid tile to retreat" in {
    val source = fromFile("saves/forIntegration/3--isThereAnyValidRetreatTiles--2024-09-18T08-45-46.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
      //      if newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Fiefdoms).head
      //        && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Fiefdoms)(5)
      if actNum >= 119//act.isInstanceOf[ActionCalculateCombatOutcome]
      then
        val a = 0
    }))

    val state = Reactor.prepareShutdown("3").currentGameState
    assert(state.subPhase.isInstanceOf[SubPhaseCleanUpAfterCombat])
  }
}
