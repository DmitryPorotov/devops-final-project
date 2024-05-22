package fwc.gameSaving.actions.planning

import fwc.game.GameState
import fwc.game.houses.HouseWolf
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.gameLoading
import fwc.gameSaving.actions.planning.ActionAddOrder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ActionAddOrderSpec extends AnyFlatSpec with should.Matchers {
  "ActionAddOrder " should "should be able to add an order" in {
    val json = gameLoading.readJson("saves/forUnitTests/initGameState.json")
    val gameState = GameState.fromJson(json)
    val gameStateAfter = ActionAddOrder(
      gameState,
      HouseWolf,
      Order(
        OrderMarch
      ),
      3
    ).doAction()
    val gameStateAfter2 = ActionAddOrder(
      gameStateAfter,
      HouseWolf,
      Order(
        OrderMarch,
        modifier = -1
      ),
      7
    ).doAction()
    val gameStateAfter3 = ActionAddOrder(
      gameStateAfter2,
      HouseWolf,
      Order(
        OrderMarch,
        true,
        1
      ),
      2
    ).doAction()
    
    assert(
      gameStateAfter3.placedOrders.placedOrders(HouseWolf).size == 3, 
      "Wolf has 3 orders placed"
    )
    assert(
      gameStateAfter3.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.contains(HouseWolf),
      "Wolf needs to confirm orders"
    )
  }
}
