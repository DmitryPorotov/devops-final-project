package fwc.gameSaving.actions.planning

import fwc.game.GameState
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.{Order, OrderType}
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
      HouseType.Wolf,
      Order(
        OrderType.March
      ),
      3
    ).doAction()
    val gameStateAfter2 = ActionAddOrder(
      gameStateAfter,
      HouseType.Wolf,
      Order(
        OrderType.March,
        modifier = -1
      ),
      7
    ).doAction()
    val gameStateAfter3 = ActionAddOrder(
      gameStateAfter2,
      HouseType.Wolf,
      Order(
        OrderType.March,
        true,
        1
      ),
      2
    ).doAction()
    
    assert(
      gameStateAfter3.placedOrders.placedOrders(HouseType.Wolf).size == 3, 
      "Wolf has 3 orders placed"
    )
    assert(
      gameStateAfter3.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.contains(HouseType.Wolf),
      "Wolf needs to confirm orders"
    )
  }
}
