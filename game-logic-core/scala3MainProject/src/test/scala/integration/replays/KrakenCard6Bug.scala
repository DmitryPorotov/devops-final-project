package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.actions.roundEvents.ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class KrakenCard6Bug extends AnyFlatSpec with should.Matchers {
  "Kraken card 6" should "be discarded after use not the card chosen when using card 6" in {
    val source = fromFile("saves/forIntegration/3--krakenCard6Bug--2024-09-17T12-23-29.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
//      if newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Fiefdoms).head
//        && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Fiefdoms)(5)
        if actNum > 525 //act.isInstanceOf[ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack]
        then
          val a = 0
    }))
    val state = Reactor.prepareShutdown("3").currentGameState
    assert(state.discardedHouseCards(HouseType.Kraken).contains(6))
    assert(!state.discardedHouseCards(HouseType.Kraken).contains(0))
  }
}
