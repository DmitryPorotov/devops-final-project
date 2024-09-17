package integration.replays

import fwc.communication.Reactor
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.board.MilitaryUnitType
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class Kraken10OrderNotRemovedAfterDefeat extends AnyFlatSpec with should.Matchers  {
  "Kraken order at tile 10" should "be removed after his defeat" in {

    val source = fromFile("saves/forIntegration/3--kraken10orderNotRemovedAfterDefeat--2024-09-15T01-10-58.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum, currentState, action, newState: GameState) =>{
      if actNum >= 220
      then
        val a = 0
      if newState.roundCounter == 3 && newState.combat != null && newState.combat.defenderHouse == HouseType.Kraken
      then
        val a = 0
    }))

    val gameState = Reactor.prepareShutdown("3").currentGameState
    assert(!gameState.placedOrders(HouseType.Kraken).exists(_._1 == 10))
//    assert(gameState.armies(40).exists(_.unitType == MilitaryUnitType.PowerToken))

  }
}
