package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.actions.roundEvents.ActionWildlingsDowngradeKnights
import fwc.game.board.MilitaryUnitType
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseResolveTiesAfterBiddingOnWildlings, SubPhaseWildlingsCard}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class KrakenGarrisonGotDeletedBug extends AnyFlatSpec with should.Matchers {
  "Kraken's garrison" should "not have being deleted" in {
    // note: not a bug? garrison not deleted. bug in bots?
    val source = fromFile("saves/forIntegration/3--krakenGarrisonGotDeletedBug--2024-09-16T02-48-30.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
//      if !newState.armies(16).exists(_.unitType == MilitaryUnitType.Garrison)
//      then
//        val a = 0

      if act.isInstanceOf[ActionWildlingsDowngradeKnights] && act.asInstanceOf[ActionWildlingsDowngradeKnights].houseType == HouseType.Rose
        then
        val a = 0
      if actNum >= 208
      then
        val a = 0
    }))

    val state = Reactor.prepareShutdown("3").currentGameState
    assert(state.armies(16).exists(_.unitType == MilitaryUnitType.Garrison))
  }
}
