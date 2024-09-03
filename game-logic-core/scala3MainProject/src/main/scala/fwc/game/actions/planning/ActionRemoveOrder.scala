package fwc.game.actions.planning

import fwc.JsonSerializable
import fwc.game.actions.Action.PlanningActions
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.{GameState, gameRules}
import fwc.game.board.{TileNumber, isValid}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.Order
import ujson.Value


case class ActionRemoveOrder (
                              gameState: GameState,
                              houseType: HouseType,
                              tileNumber: TileNumber
  ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {


  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseAddOrder]
    then throw new ActionException("Wrong phase")

    if !gameState.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.contains(houseType)
    then throw new ActionException("You have already confirmed your orders")

    if !tileNumber.isValid
    then throw new ActionException(s"Invalid tile number $tileNumber")
    
    val order = gameState.placedOrders.getOrderByTileNumber(tileNumber)
    if order.isEmpty 
    then throw new ActionException(s"This tile number $tileNumber has no order placed")
      
    if order.head._1 != houseType
    then throw new ActionException(s"This order does not belong to you")

    val updatedOrders = gameState.placedOrders.removeOrder(houseType, tileNumber)

    val updatedAvOrders = (gameState.availableOrders.returnOrder _).tupled(order.head)

    gameState.copy(
      placedOrders = updatedOrders,
      availableOrders = updatedAvOrders
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> PlanningActions.RemoveOrder.string,
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber
  )
}

object ActionRemoveOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: ujson.Value): ActionRemoveOrder = {
    ActionRemoveOrder(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt
    )
  }
}