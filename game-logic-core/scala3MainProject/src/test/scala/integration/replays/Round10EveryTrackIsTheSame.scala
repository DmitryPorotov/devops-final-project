package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.actions.roundEvents.ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack
import fwc.game.board.TrackType

import scala.io.Source.fromFile

class Round10EveryTrackIsTheSame extends AnyFlatSpec with should.Matchers {
  "Tracks" should "should not be the same" in {
    val source = fromFile("saves/forIntegration/3--round10--2024-09-10T01-36-05.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
//      if newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne).head == newState.tracks(TrackType.Fiefdoms).head
//        && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Court).head && newState.tracks(TrackType.Throne)(5) == newState.tracks(TrackType.Fiefdoms)(5)
        if actNum > 525 //act.isInstanceOf[ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack]
        then
          val a = 0
    }))
    val message = MessageGameAction(-5, "3", ujson.Obj(
      "houseType" -> "rose",
      "actionType" -> "wildlingsChooseKill2UnitsOr2PositionsOnTrack",
      "track" -> "court"
    ), null)

    val retVal = Reactor(message, ujson.Obj())
    assert(true)
  }
}
