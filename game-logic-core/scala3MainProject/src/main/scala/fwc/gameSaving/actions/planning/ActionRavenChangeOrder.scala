package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.actionPhase.DominanceTokensUsage
import fwc.game.board.{DominanceTokenMessengerRaven, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseRavenChangeOrder
import fwc.game.planningPhase.{Order, OrderType}
import fwc.game.{GameState, gameRules}
import fwc.gameSaving.actions.action.NextOrderFinder
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
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

    val newPhase = NextOrderFinder.nextSubPhase(
      newGameState, OrderType.Raid
    )

    ActionAddOrder(newGameState, houseType, order, tileNumber).doPlaceOrder().copy(
      subPhase = newPhase,
      dominanceTokensUsage = DominanceTokensUsage(
        newGameState.dominanceTokensUsage.usage + (DominanceTokenMessengerRaven -> true)
      )
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
