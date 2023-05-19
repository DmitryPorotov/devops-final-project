package fwc.gameSaving.actions.planning

import fwc.game.GameState
import fwc.game.houses.HouseWolf
import fwc.game.phases.planningSubPhases.SubPhaseReadyToOpenOrders
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.gameLoading
import fwc.gameSaving.actions.planning.ActionAddOrder
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class ActionAddLastOrderSpec extends AnyFlatSpec with should.Matchers {

  "ActionAddOrder" should "switch to ready to open orders when adding last order" in {
    val json = gameLoading.readJson("saves/forUnitTests/OneOrderLeftToAdd.json")
    val gameState = GameState.fromJson(json)
    val action = ActionAddOrder(
      gameState,
      HouseWolf,
      Order(
        OrderMarch
      ),
      2
    )
    val newGameState = action.doAction()

    assert(newGameState.subPhase.isInstanceOf[SubPhaseReadyToOpenOrders])
    assert(newGameState.subPhase.asInstanceOf[SubPhaseReadyToOpenOrders].houseTypes.size == 6)
    assert(newGameState.subPhase.asInstanceOf[SubPhaseReadyToOpenOrders].getSubPhaseName == "readyToOpenOrders")

  }
}
