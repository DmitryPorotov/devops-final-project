package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.{Armies, MilitaryUnit, TileNumber}
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveSpecialConsolidatePower
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveSpecialConsolidatePower(
                                                 gameState: GameState,
                                                 houseType: HouseType,
                                                 tileNumber: TileNumber,
                                                 unit: MilitaryUnit
                                               )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveSpecialConsolidatePower]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseResolveSpecialConsolidatePower].houseType != houseType
    then throw new ActionException("Wrong house")

    val orderOpt = gameState.placedOrders.getOrderByTileNumber(tileNumber)
    val order =
      if orderOpt.isEmpty
      then throw new ActionException(s"There is no order at tile $tileNumber")
      else orderOpt.head

    if order._1 != houseType || order._2.orderType != OrderType.OrderConsolidatePower || !order._2.isStar
    then throw new ActionException(s"There is no special consolidate power order of house $houseType at tile $tileNumber")

    val tile = gameRules.board(tileNumber)

    val updatedPlacedOrders = gameState.placedOrders.removeOrder(houseType, tileNumber)

    val updatedGameState =
      gameState.copy(
        placedOrders = updatedPlacedOrders
      )

    val updatedGameState2 =
      updatedGameState.copy(
        subPhase = NextOrderFinder.nextSubPhase(updatedGameState, OrderType.OrderConsolidatePower, houseType)
      )

    if unit == null
    then updatedGameState2.copy(
      powerTokens = gameState.powerTokens.addTokens(
        houseType,
        1 + tile.powerPoints,
        gameState.armies)
    )
    else {
      val (armies: Armies, _) = Mustering.musterGroundUnit(tileNumber, unit, updatedGameState2)
      updatedGameState2.copy(
        armies = armies
      )
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveSpecialConsolidatePower",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber,
    "unit" -> unit.toJson
  )
}

object ActionResolveSpecialConsolidatePower extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveSpecialConsolidatePower =
    ActionResolveSpecialConsolidatePower(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt,
      MilitaryUnit.fromJson(json("unit"))
    )
}
