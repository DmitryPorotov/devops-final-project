package integration.replays

import fwc.communication.Reactor
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class MooseCard3DiscardsOwnCardBug extends AnyFlatSpec with should.Matchers  {
  "Moose card 3" should "discard opponents card not his own" in {

    val source = fromFile("saves/forIntegration/3--mooseCard3DiscardsOwnCardBug--2024-09-16T09-39-03.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines)

    val gameState = Reactor.prepareShutdown("3").currentGameState
    assert(gameState.discardedHouseCards(HouseType.PufferFish).contains(1))
    assert(!gameState.discardedHouseCards(HouseType.Moose).contains(1))

  }
}
