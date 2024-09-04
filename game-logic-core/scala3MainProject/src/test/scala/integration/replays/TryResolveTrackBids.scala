package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class TryResolveTrackBids extends AnyFlatSpec with should.Matchers  {
  "Try to resolve track bids with wrong solution" should "return a proper error" in {

    val source = fromFile("saves/forIntegration/3--tryResolvetrackBids--2024-09-04T08-31-25.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val message = MessageGameAction(-6, "3",
      ujson.Obj(
        "houseType" -> "lion", "actionType" -> "resolveTiesAfterBiddingOnTracks", "resolution" -> ujson.Arr.from(Seq("pufferfish", "kraken", "wolf", "moose", "rose", "lion"))
      ),
      null)
    val result = Reactor(message, ujson.Obj())
    assert(result("message").str.contains("is invalid"))

  }
}
