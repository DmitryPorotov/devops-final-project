package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.eventsPhase.{Mustering, UsedMusteringPoints}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveSpecialConsolidatePower
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

import scala.util.Try

case class ActionResolveSpecialConsolidatePower(
                                                 gameState: GameState,
                                                 houseType: HouseType,
                                                 unitToMuster: Option[MilitaryUnit],
                                                 fromTile: TileNumber,
                                                 toTile: Option[TileNumber],
                                                 isUpgrade: Boolean
                                               )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveSpecialConsolidatePower]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseResolveSpecialConsolidatePower].houseType != houseType
    then throw new ActionException("Wrong house")

    val orderOpt = gameState.placedOrders.getOrderByTileNumber(fromTile)
    val order =
      if orderOpt.isEmpty
      then throw new ActionException(s"There is no order at tile $fromTile")
      else orderOpt.head

    if order._1 != houseType || order._2.orderType != OrderType.ConsolidatePower || !order._2.isStar
    then throw new ActionException(s"There is no special consolidate power order of house $houseType at tile $fromTile")

    val tile = gameRules.board(fromTile)

    val updatedPlacedOrders = gameState.placedOrders.removeOrder(houseType, fromTile)

    val updatedGameState =
      gameState.copy(
        placedOrders = updatedPlacedOrders
      )

    val updatedGameState2 =
      updatedGameState.copy(
        subPhase = NextOrderFinder.nextSubPhase(updatedGameState, OrderType.ConsolidatePower, houseType)
      )

    if unitToMuster.isEmpty
    then updatedGameState2.copy(
      powerTokens = gameState.powerTokens.addTokens(
        houseType,
        1 + tile.powerPoints,
        gameState.armies)
    )
    else {
      val (ar: Armies, usedMustPoints: UsedMusteringPoints) =
        if unitToMuster.head.unitType == MilitaryUnitType.Ships
        then 
          if toTile.isEmpty
          then throw new ActionException("toTile should not be empty")
          else Mustering.musterShips(fromTile, toTile.head, unitToMuster.head, gameState)
        else Mustering.musterGroundUnit(fromTile, unitToMuster.head, gameState, isUpgrade)
      updatedGameState2.copy(
        armies = ar,
        usedMusteringPoints = usedMustPoints,
      )
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveSpecialConsolidatePower",
    "houseType" -> houseType.toString,
    "fromTile" -> fromTile,
    "unitToMuster" -> (if unitToMuster.nonEmpty then unitToMuster.head.toJson else ujson.Null),
    "toTile" -> (if toTile.nonEmpty then toTile.head else ujson.Null),
    "isUpgrade" -> isUpgrade
  )
}

object ActionResolveSpecialConsolidatePower extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveSpecialConsolidatePower =
    ActionResolveSpecialConsolidatePower(
      gameState,
      HouseType.fromString(json("houseType").str),
      Try(Some(MilitaryUnit.fromJson(json("unitToMuster").obj))).getOrElse(None),
      json("fromTile").num.toInt,
      Try(Some(json("toTile").num.toInt)).getOrElse(None),
      json("isUpgrade").bool
    )
}
