package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.phases.roundEventsSubPhases.{SubPhaseResolveTiesAfterBiddingOnWildlings, SubPhaseWildlingsCard}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Wildlings5CardStuckBug extends AnyFlatSpec with should.Matchers {
  "The game" should "not get stuck after resolving wildlings card 5 as win" in {
    // note: not a bug? does not get stuck. bug in bots?
    val source = fromFile("saves/forIntegration/3--wildlings5CardStuckBug--2024-09-16T12-55-00.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
      if oldState.subPhase.isInstanceOf[SubPhaseResolveTiesAfterBiddingOnWildlings]
      then
        val a = 0
      if actNum >= 58
      then
        val a = 0
    }))

    val phase = Reactor.prepareShutdown("3").currentGameState.subPhase
    assert(!phase.isInstanceOf[SubPhaseWildlingsCard])
  }
}
