package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseResolveTiesAfterBiddingOnWildlings, SubPhaseWildlingsCard}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class RoseSuppliesAreWrong extends AnyFlatSpec with should.Matchers {
  "Rose supplies" should "be correct" in {
    // note: not a bug? bug in bots?
    val source = fromFile("saves/forIntegration/3--roseSuppliesAreWrong--2024-09-18T10-18-28.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
      if oldState.supplies(HouseType.Rose) != newState.supplies(HouseType.Rose)
      then
        val a = 0
      if actNum >= 10
      then
        val a = 0
    }))

    val phase = Reactor.prepareShutdown("3").currentGameState.subPhase
    assert(!phase.isInstanceOf[SubPhaseWildlingsCard])
  }
}
