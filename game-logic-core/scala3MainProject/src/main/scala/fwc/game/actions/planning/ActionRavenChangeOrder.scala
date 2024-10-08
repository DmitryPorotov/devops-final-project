package fwc.game.actions.planning

import fwc.JsonSerializable
import fwc.game.actionPhase.DominanceTokensUsage
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.action.NextOrderFinder
import fwc.game.board.{DominanceTokenType, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseRavenChangeOrder
import fwc.game.planningPhase.{Order, OrderType}
import fwc.game.{GameState, gameRules}
import ujson.Value

case class ActionRavenChangeOrder(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   order: Order,
                                   tileNumber: TileNumber
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseRavenChangeOrder]
    then throw new ActionException("Wrong phase")

    if gameState.tracks.ravenOwner != houseType
    then throw new ActionException(s"House $houseType does has not Messenger Raven token")

    val newGameState = if gameState.placedOrders.placedOrders(houseType).contains(tileNumber)
    then
      val order = gameState.placedOrders.placedOrders(houseType)(tileNumber)
      gameState.copy(
        placedOrders = gameState.placedOrders.removeOrder(houseType, tileNumber),
        availableOrders = gameState.availableOrders.returnOrder(houseType, order)
      )
    else throw new ActionException(s"There is no order at ${gameRules.board(tileNumber).name} ($tileNumber)")

    val newGameState1 = ActionAddOrder(newGameState, houseType, order, tileNumber).doPlaceOrder().copy(
      dominanceTokensUsage = DominanceTokensUsage(
        newGameState.dominanceTokensUsage.usage + (DominanceTokenType.MessengerRaven -> true)
      )
    )
    val newPhase = NextOrderFinder.nextSubPhase(
      newGameState1, OrderType.Raid
    )
    newGameState1.copy(
      subPhase = newPhase
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> ujson.Str("ravenChangeOrder"),
    "order" -> order.toJson,
    "houseType" -> ujson.Str(houseType.toString),
    "tileNumber" -> ujson.Num(tileNumber)
  )
}

object ActionRavenChangeOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRavenChangeOrder = {
    ActionRavenChangeOrder(
      gameState,
      HouseType.fromString(json("houseType").str),
      Order.fromJson(json("order")),
      json("tileNumber").num.toInt
    )
  }
}
