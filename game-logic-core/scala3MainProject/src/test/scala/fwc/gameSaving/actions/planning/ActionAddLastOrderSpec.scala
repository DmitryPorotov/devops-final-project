package fwc.gameSaving.actions.planning

import fwc.game.GameState
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.{SubPhaseAddOrder, SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard, SubPhaseReadyToOpenOrders}
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameLoading
import fwc.gameSaving.actions.planning.ActionAddOrder
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class ActionAddLastOrderSpec extends AnyFlatSpec with should.Matchers {

  "ActionAddOrder" should "enable to confirm orders when adding the last order" in {
    val json = gameLoading.readJson("saves/forUnitTests/OneOrderLeftToAdd.json")
    val gameState = GameState.fromJson(json)
    val action = ActionAddOrder(
      gameState,
      HouseType.Wolf,
      Order(
        OrderType.March
      ),
      2
    )
    val updatedGameState = action.doAction()

    assert(updatedGameState.subPhase.isInstanceOf[SubPhaseAddOrder])
    assert(updatedGameState.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.size == 1)

    val action2 = ActionOpenOrders(updatedGameState, HouseType.Wolf)
    val updatedGameState2 = action2.doAction()
    assert(updatedGameState2.subPhase.isInstanceOf[SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard])
  }
}
