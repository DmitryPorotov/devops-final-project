package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.roundEventsSubPhases.{SubPhaseOpenTrackBids, SubPhaseTracksBids}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class TrackBidsFinish  extends AnyFlatSpec with should.Matchers  {
  "Last bid on tracks" should "switch to open track bids" in {

    val source = fromFile("saves/forIntegration/3--trackBids--2024-08-31T06-01-22.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)


    val phase = Reactor.prepareShutdown("3").currentGameState.subPhase
    assert(phase.isInstanceOf[SubPhaseOpenTrackBids])
  }
}
