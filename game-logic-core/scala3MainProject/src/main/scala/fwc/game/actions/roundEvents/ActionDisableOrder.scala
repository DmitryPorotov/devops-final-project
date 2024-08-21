package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.*
import ujson.Value

case class ActionDisableOrder(
                             gameState: GameState,
                             orderType: OrderType,
                           ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {


    val updatedAvailAbleOrders =
      if orderType == OrderType.March
      then gameState.availableOrders.disableMarchPlusOneOrder()
      else gameState.availableOrders.disableOrderType(orderType)

    gameState.copy(
      subPhase = SubPhaseAddOrder(HouseType.getSeqOfAll),
      availableOrders = updatedAvailAbleOrders
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "disableOrder",
    "orderType" -> orderType.toString
  )
}

object ActionDisableOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionDisableOrder =
    ActionDisableOrder(
      gameState,
      OrderType.fromString(json("orderType").str)
    )
}